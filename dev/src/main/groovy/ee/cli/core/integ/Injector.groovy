package ee.cli.core.integ

interface Injector {
    def resolve(String fullClassName)

    def resolve(Class type)

    def resolve(String fullClassName, boolean forceNew)

    def resolve(Class type, boolean forceNew)

    def resolve(String className, boolean forceNew, Map<String, Object> props)

    def resolve(String className, boolean forceNew, Map<String, Object> props, Map<String, Closure> customResolver)

    def resolve(Class clazz, boolean forceNew, Map<String, Object> props)

    def resolve(Class clazz, boolean forceNew, Map<String, Object> props, Map<String, Closure> customResolver)

    def wire(def item)

    def wire(def item, Class type)

    def wire(def item, Class type, boolean forceNew)

    def wire(def item, Class type, boolean forceNew, Map<String, Object> props)

    def wire(def item, Class type, boolean forceNew, Map<String, Object> props, Map<String, Closure> customResolver)

    Map<String, Object> calculateChanges(def item)

    Map<String, Object> calculateChanges(def item, boolean newItem)

    void registerClassResolver(Class type, Closure resolver)

    void registerExtension(Class type, Class<? extends Extension> extensionType)
}
