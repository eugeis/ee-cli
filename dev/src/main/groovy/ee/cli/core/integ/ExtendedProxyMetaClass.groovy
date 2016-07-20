package ee.cli.core.integ

import java.beans.IntrospectionException

class ExtendedProxyMetaClass extends ProxyMetaClass {
    protected Map<String, Object> instanceStore

    public ExtendedProxyMetaClass(MetaClassRegistry registry, Class theClass, MetaClass adaptee, Interceptor interceptor) throws IntrospectionException {
        super(registry, theClass, adaptee)
        this.interceptor = interceptor
    }

    void instanceStore(String key, def value) {
        if (instanceStore == null) {
            instanceStore = new HashMap<>()
        }
        instanceStore[key] = value
    }

    def instanceStore(String key) {
        if (instanceStore != null) {
            instanceStore[key]
        }
    }
}
