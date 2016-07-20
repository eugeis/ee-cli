package ee.cli.core.integ

import ee.cli.core.Valid
import ee.cli.core.integ.settings.JsonSettings
import ee.cli.core.model.*
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class JsonModelController extends Base {

    @Inject
    @NotNull
    Label label

    @Inject
    Injector injector

    @Inject
    @NotNull
    Workspace workspace

    Map model

    File jsonFile

    @PostConstruct
    @Valid
    void init() {
        JsonSlurper parser = new JsonSlurper()

        jsonFile = new File(workspace.ee, "${label.name}.json")
        if (jsonFile.exists()) {
            log.info("Load {} configuration for {}", label, workspace.home)
            try {
                model = parser.parse(jsonFile)
                log.debug "${label.name} {}", model
            } catch (e) {
                log.error 'The file {} for configuration of {} can not be loaded because of exception {}.', jsonFile, label, e
                initEmptyModel()
            }
        } else {
            initEmptyModel()
        }
    }

    protected void initEmptyModel() {
        log.info 'Create new file {} for configuration of {}.', jsonFile, label
        model = [:]
        writeModel()
    }

    void applyModelChanges(def item, boolean newItem = false) {
        Map changes = injector.calculateChanges(item, newItem)
        changes.remove(JsonSettings.KEY_OF_NAME)
        Map entry = model[item.name]
        if (changes || !entry) {
            if (entry) {
                entry.putAll(changes)
            } else {
                entry = changes
            }
            model[item.name] = entry
            writeModel()
        }
    }

    def data(String name) {
        model[name]
    }

    def find(Closure selector) {
        model.find(selector)
    }

    def findByName(String itemName) {
        find { String name, def itemDef -> itemName.equalsIgnoreCase(name) }
    }

    def removeByName(String itemName) {
        model.remove(itemName)
    }

    def findByNumber(int number) {
        findByName(model.keySet().getAt(number - 1))
    }

    def findAll(Closure selector) {
        model.findAll(selector)
    }

    List<String> getSortedKeys() {
        new ArrayList<>(model.keySet()).sort()
    }

    void writeModel() {
        def json = JsonOutput.toJson(model)
        json = JsonOutput.prettyPrint(json)

        if (!jsonFile.parentFile.exists()) {
            jsonFile.parentFile.mkdirs()
        }

        jsonFile.write(json)
    }
}
