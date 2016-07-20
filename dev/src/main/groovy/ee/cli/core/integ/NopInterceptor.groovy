package ee.cli.core.integ

import ee.cli.core.Nop

import javax.inject.Inject

class NopInterceptor extends AnnotationBasedInterceptor implements Interceptor {
    @Inject
    boolean nop = false

    Object beforeInvoke(Object obj, String methodName, Object[] args) {
        def ret
        if (nop && isMethodAnnotated(methodName, args)) {
            log.info '(nop)-> {}.{}({})', obj, methodName, args
            doInvoke = false
        } else {
            doInvoke = true
        }
        if (!doInvoke && methodName.equalsIgnoreCase('asBoolean')) {
            ret = false
        }
        ret
    }

    @Override
    protected Class getAnnotation() {
        Nop
    }
}
