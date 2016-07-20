package ee.cli.core.integ

import ee.cli.core.model.Base

class InterceptorChain extends Base implements Interceptor {
    protected List<Interceptor> interceptors = []
    boolean doInvoke = true

    Object beforeInvoke(Object obj, String methodName, Object[] args) {
        def ret
        doInvoke = true
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeInvoke(obj, methodName, args)
            if(!interceptor.doInvoke()) {
                doInvoke = false
                break;
            }
        }
        if (!doInvoke && methodName.equalsIgnoreCase('asBoolean')) {
            ret = false
        }
        ret
    }

    boolean doInvoke() {
        doInvoke
    }

    Object afterInvoke(Object object, String methodName, Object[] args, Object result) {
        def ret = result
        for (Interceptor interceptor : interceptors) {
            ret = interceptor.afterInvoke(object, methodName, args, ret)
        }
        ret
    }

    void addInterceptor(Interceptor interceptor) {
        interceptors << interceptor
    }
}
