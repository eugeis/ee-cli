package ee.cli.core.integ

import ee.cli.core.Valid
import ee.cli.core.integ.api.Clazz
import ee.cli.core.integ.settings.CustomSettings
import ee.cli.core.integ.settings.JsonSettings
import ee.cli.core.integ.settings.SettingsBase
import ee.cli.core.model.Command
import ee.cli.core.model.Context
import ee.cli.core.model.Environment
import ee.cli.core.model.Result
import ee.cli.core.model.Settings

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class JsonModelBasedHandler extends ClassBasedHandler {
    @Inject
    Clazz clazz

    @Inject
    @NotNull
    Environment env

    JsonModelController model

    @PostConstruct
    @Valid
    void init() {
        model = injector.resolve(JsonModelController, true, ['label': label])
    }

    @Override
    protected Result execute(def item, Command command, Context context, Object source) {
        Result ret = super.execute(item, command, context, source)
        applyModelChanges(item)
        ret
    }

    void applyModelChanges(def item, boolean newItem = false) {
        model.applyModelChanges(item, newItem)
        //refresh resolved
        resolved[item.name] = item
    }

    protected def doResolve(String itemName) {
        def item = model.findByName(itemName)
        if(!item && itemName.isInteger()) {
            item = model.findByNumber(Integer.parseInt(itemName))
        }
        doResolveEntry item
    }

    @Valid
    def resolve(Class itemType) {
        //resolve first
        doResolveEntry(model.find { String name, def itemDef ->
            String className = resolveClassName(itemDef.type)
            className == itemType.name
        })
    }

    protected def doResolveEntry(Map.Entry entry) {
        def ret
        if (entry) {
            String itemName = entry.key
            if (!resolved.containsKey(itemName)) {
                def itemDef = entry.value
                if (!itemDef.type) {
                    throw new IllegalArgumentException("Type is not defined in the model item $itemDef")
                }
                String className = resolveClassName(itemDef.type)
                ret = injector.resolve(className, true, null,
                        ['settings': { instance, target -> buidlSettings(itemName, itemDef) }]
                )
            } else {
                ret = resolved[itemName]
            }
        }
        ret
    }

    protected Settings buidlSettings(String name, def props) {
        env.settings(new JsonSettings(key: "${label.name}.$name", name: name, props: props))
    }

    protected void fillExportedParams(def item,
                                      Map<String, Object> fillExportedParams) {
        def fields = clazz.findAnnotatedFields(item.class, Export)
        if (fields) {
            String prefix = item.category ?: item.name
            fields.each { field ->
                fillExportedParams["${prefix}.$field.name"] = item."$field.name"
            }
        }
    }

    def add(def settingsProps, SettingsBase settingsBase = env.base) {
        fillName(settingsProps)
        Settings settings = env.settings(settingsBase, new CustomSettings(props: settingsProps))

        String type = findType(settings)
        String className = resolveClassName(type)

        def settingsResolver = { instance, target -> settings }

        def ret = injector.resolve(className, true, null, ['settings': settingsResolver])
        applyModelChanges(ret, true)
        model.data(ret.name).type = type
        writeModel()
        ret
    }

    def remove(def settingsProps) {
        fillName(settingsProps)
        if(settingsProps.name) {
            model.removeByName(settingsProps.name)
            writeModel()
        } else {
            log.info("Please provide the name of entry to remove")
        }
    }

    protected void fillName(settingsProps) {
        if (!settingsProps.name) {
            def name = settingsProps.find { k, v -> v == null || v == '' }?.key
            if(name) {
                settingsProps.name = name
                settingsProps.remove(name)
            }
        }
    }

    def show() {
        def sortedKeys = model.sortedKeys
        sortedKeys.eachWithIndex { String b, i -> log.info("${i+1}: ${b.padRight(25)} - ${model.data(b).desc ?:''}") }
    }

    protected String findType(Settings settings) {
        settings.get('type')
    }

    protected void writeModel() {
        model.writeModel()
    }
}
