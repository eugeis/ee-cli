package ee.cli.core.integ

import ee.cli.core.integ.api.Clazz
import ee.cli.core.integ.converter.ComponentModelConverter
import ee.cli.core.integ.converter.EnumConverter
import ee.cli.core.integ.converter.MapConverter
import ee.cli.core.integ.converter.MavenCoordinateConverter
import ee.cli.core.integ.converter.PackageConverter
import ee.cli.core.integ.converter.RelativeFileConverter
import ee.cli.core.integ.settings.CustomSettings
import ee.cli.core.integ.settings.KeySettings
import ee.cli.core.model.Base
import ee.cli.core.model.Environment
import ee.cli.core.model.Label
import ee.cli.core.model.Package
import ee.cli.core.model.Settings
import ee.cli.core.model.Tool
import ee.cli.dsl.ComponentModel
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.AttributeConverter
import javax.persistence.Convert
import java.lang.reflect.Field
import java.lang.reflect.Method

class InjectorImpl extends Base implements Injector {
    protected Map<Class, Class> globalConverter = [:]
    protected Map<Class, Class> localConverter = [:]
    protected Map<Class, Object> resolved = [:]
    protected Map<String, Closure> resolvers = [:]
    protected Map<Class, Closure> clazzResolvers = [:]
    protected Map<Class, List<Class<? extends Extension>>> typeToExtensions = [:]
    protected Set<Class<? extends Extension>> loadedExtensions = [] as Set
    protected WeakHashMap<Object, Object> localInstanceSettings = new WeakHashMap<>()
    protected HandlersHandler handlersHandler

    protected Closure clazzResolver = { instance, Field target ->
        def ret
        Class customClassResolver = clazzResolvers.keySet().find { it.isAssignableFrom(target.type) }
        if (customClassResolver) {
            ret = clazzResolvers[customClassResolver](target.type)
        } else {
            ret = resolve(target.type)
        }
        ret
    }

    protected Clazz classUtils = new Clazz()
    protected Environment env

    void init() {
        addLocalConverter(File, RelativeFileConverter)
        addLocalConverter(Enum, EnumConverter)
        addLocalConverter(Map, MapConverter)
        addLocalConverter(ComponentModel, ComponentModelConverter)
        addGlobalConverter(MavenCoordinate, MavenCoordinateConverter)
        addGlobalConverter(Package, PackageConverter)
    }

    @Override
    def resolve(String fullClassName, boolean forceNew = false, Map<String, Object> props = null, Map<String, Closure> customResolver = null) {
        def ret
        Class clazz = fullClassName.asClass()
        if (clazz) {
            ret = resolve(clazz, forceNew, props, customResolver)
        }
        ret
    }

    @Override
    def resolve(Class type, boolean forceNew = false, Map<String, Object> props = null, Map<String, Closure> customResolver = null) {
        def ret
        if (forceNew || !resolved.containsKey(type)) {
            ret = wire(type.newInstance(), type, forceNew, props, customResolver)
        } else {
            ret = resolved[type]
        }
        ret
    }

    @Override
    def wire(
            def item, Class type = item.class, boolean forceNew = false, Map<String, Object> props = null, Map<String, Closure> customResolver = null) {
        loadExtension(type)
        def ret = wireInterceptors(item)
        if (!forceNew) {
            resolved[type] = ret
        }

        if (ret == null) {
            throw new IllegalArgumentException("Item is null, item: $item, type=$type, forceNew: $forceNew, customResolver: $customResolver")
        }

        def fields = this.classUtils.findAnnotatedFields(type, Inject)
        if (fields) {

            //store local settings by instance
            def localSettings = [:]
            localInstanceSettings.put(ret, localSettings)

            customResolver = fillPropsAsSettingsToCustomResolver(props, ret, customResolver)

            Settings settings = customResolver?.containsKey('settings') ?
                    customResolver['settings'](ret, null) : env.settings(ret.buildLabel())
            Closure settingsResolver = { instance, Field target -> settings.get(target.name) }

            fields.each { field ->
                try {
                    boolean local = isLocal(field)
                    Closure resolver = chooseResolver(local, field, customResolver, settingsResolver)
                    def value = inject(ret, field, resolver)
                    if (local) {
                        localSettings[field] = value
                    }
                } catch (e) {
                    log.error('Exception at resolving of {} in {}', field, ret, e)
                }
            }
        }
        postConstruct(type, ret)
        ret
    }

    protected Map<String, Closure> fillPropsAsSettingsToCustomResolver(Map<String, Object> props,
                                                                       def item, Map<String, Closure> customResolver) {
        if (props) {
            if (!customResolver) {
                customResolver = [:]
            }

            if (customResolver.containsKey('settings')) {
                KeySettings settings = customResolver['settings'](item, null)
                customResolver.settings = { instance, target ->
                    env.settings(settings, new CustomSettings(props: props))
                }
            } else {
                customResolver.settings = { instance, target ->
                    env.settings(new CustomSettings(props: props))
                }
            }

        }
        customResolver
    }

    Map<String, Object> calculateChanges(def item, boolean newItem = false) {
        Map<String, Object> ret

        Map<Field, Object> localSettings = localInstanceSettings.get(item)
        if (localSettings) {
            ret = [:]
            Map<Field, Object> changes = [:]
            localSettings.each { def field, value ->
                def newValue = item."$field.name"
                if (newItem || value != newValue) {
                    log.debug('The field {} value changed from {} to {} in {},', field, value, newValue)
                    changes[field] = newValue
                }
            }

            //update local settings
            localSettings.putAll(changes)

            changes.each { def field, value ->
                def persistentValue
                if (value != null) {
                    AttributeConverter converter = resolveConverter(field, item)
                    if (converter) {
                        persistentValue = converter.convertToDatabaseColumn(value)
                    } else {
                        persistentValue = value
                    }
                }
                ret[field.name] = persistentValue
            }
        }
        ret
    }

    def wireInterceptors(def item) {
        Class clazz = item.class
        def interceptors = classUtils.findInterceptors(clazz)
        if (interceptors) {
            Interceptor interceptor
            def customPropsOfInterceptor = ['targetClass': clazz]
            if (interceptors.size() > 1) {
                InterceptorChain interceptorChain = new InterceptorChain()
                interceptors?.each { Class interceptorClass ->
                    def currentInterceptor = resolve(interceptorClass, true, customPropsOfInterceptor)
                    interceptorChain.addInterceptor(currentInterceptor)
                }
                interceptor = interceptorChain
            } else {
                interceptor = resolve(interceptors[0], true, customPropsOfInterceptor)
            }

            ProxyMetaClass proxy = ProxyMetaClass.getInstance(clazz)
            proxy.interceptor = interceptor
            item.setMetaClass(proxy);
        }
        item
    }

    private Closure chooseResolver(boolean local, Field field, Map<String, Closure> customResolver, Closure settingsResolver) {
        Closure ret
        String key = local ? field.name : field.type.getName().toString()

        if (customResolver && customResolver.containsKey(key)) {
            ret = customResolver[key]
        } else if (resolvers.containsKey(key)) {
            ret = resolvers[key]
        } else if (local) {
            ret = settingsResolver
        } else {
            ret = clazzResolver
        }
        ret
    }


    private def inject(def item, Field field, Closure resolver) {
        def ret
        if (resolver) {
            def value = resolver(item, field)
            if (value != null) {
                AttributeConverter converter = resolveConverter(field, item)
                if (converter && String.isInstance(value)) {
                    ret = converter.convertToEntityAttribute(value)
                } else {
                    ret = value
                }
                try {
                    log.debug('Inject {} to {} in {},', ret, field, item)
                    item."$field.name" = ret
                } catch (e) {
                    log.error('Exception at injection of {} to {} in {}', ret, field, item, e)
                }
            }
        }
        if (!ret) {
            log.debug('Cannot resolve {} in {},', field, item)
        }
        ret
    }

    @Override
    void registerClassResolver(Class type, Closure resolver) {
        clazzResolvers[type] = resolver
    }

    @Override
    void registerExtension(Class type, Class<? extends Extension> extensionType) {
        if (typeToExtensions[type] == null) {
            typeToExtensions[type] = []
        }
        typeToExtensions[type] << extensionType
    }

    protected void loadExtension(Class type) {
        List<Class> extensionTypes = typeToExtensions[type]
        if (extensionTypes && !loadedExtensions.containsAll(extensionTypes)) {
            extensionTypes.findAll { !loadedExtensions.contains(it) }.each { extensionType ->
                loadExtentionType(extensionType)
            }
        }
    }

    void loadExtentionType(Class extensionType) {
        loadedExtensions << extensionType
        Extension extension = resolve(extensionType)
        extension.extend()
    }

    protected AttributeConverter resolveConverter(Field field, item) {
        def ret
        Class converterClass = field.isAnnotationPresent(Convert) ?
                field.getAnnotation(Convert).converter() : resolveGlobalConverterClass(field)
        if (converterClass) {
            ret = resolve(converterClass, false, ['item': item, 'target': field])
        } else {
            converterClass = resolveLocalConverterClass(field)
            if (converterClass) {
                ret = resolve(converterClass, true, ['item': item, 'target': field])
            }
        }
        ret
    }

    protected Class resolveLocalConverterClass(Field field) {
        localConverter.find { k, v -> k.isAssignableFrom(field.type) }?.value
    }

    protected Class resolveGlobalConverterClass(Field field) {
        globalConverter.find { k, v -> k.isAssignableFrom(field.type) }?.value
    }

    private void postConstruct(Class clazz, ret) {
        def calledAlready = []
        def postConstructs = this.classUtils.findAnnotatedMethods(clazz, PostConstruct)
        postConstructs.each { Method method ->
            if (!calledAlready.contains(method.name)) {
                calledAlready << method.name
                try {
                    ret."$method.name"()
                } catch (e) {
                    log.error 'Post construct call of {} in {} caused exception {}.', method, ret, e
                }
            }
        }
    }


    protected boolean isLocal(Field field) {
        def type = field.type
        String typeName = type.name
        boolean ret = type.isPrimitive() || type.isEnum() ||
                typeName.startsWith('java.lang') || typeName.startsWith('groovy.lang') ||
                [File, MavenCoordinate, Package, Label, Collection, Map, ComponentModel, Object[]].any {
                    it.isAssignableFrom(type)
                }
        ret
    }

    void addResolver(String name, Closure resolver) {
        if (!resolvers.containsKey(name)) {
            resolvers[name] = resolver
        } else {
            log.error('Skip adding of the resolver for "{}", because it exists already.', name)
        }
    }

    void addGlobalConverter(Class targetType, Class<? extends AttributeConverter> converterClass) {
        globalConverter[targetType] = converterClass
    }

    void addLocalConverter(Class targetType, Class<? extends AttributeConverter> converterClass) {
        localConverter[targetType] = converterClass
    }

    void setEnv(Environment env) {
        this.env = env
        init(env)
    }

    protected void init(Environment env) {
        addCommonResolver()
        addEnvResolver()
    }

    protected void addCommonResolver() {
        final def injector = this
        addResolver('ee.cli.core.integ.Injector', { Object instance, def target = null -> injector })
        addResolver('ee.cli.core.integ.api.Clazz', { Object instance, def target = null -> classUtils })
    }

    protected void addEnvResolver() {
        addResolver('ee.cli.core.model.Environment', { Object instance, def target = null -> env })
        addResolver('ee.cli.core.model.Settings', { Object instance,
                                                    def target = null -> env.settings(instance.label())
        })
    }
}
