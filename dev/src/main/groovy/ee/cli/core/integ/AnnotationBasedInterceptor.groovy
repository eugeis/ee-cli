package ee.cli.core.integ

import ee.cli.core.integ.api.Clazz
import ee.cli.core.model.Base

import javax.annotation.PostConstruct
import javax.inject.Inject

abstract class AnnotationBasedInterceptor extends Base implements Interceptor {
    @Inject
    Clazz clazz
    @Inject
    Class targetClass
    Set<String> annotatedMethods
    boolean doInvoke = true

    @PostConstruct
    void init() {
        annotatedMethods = clazz.findAnnotatedMethods(targetClass, getAnnotation())?.collect { it.name } as Set
    }

    protected boolean isMethodAnnotated(String methodName, Object[] args) {
        annotatedMethods.contains(methodName)
    }

    boolean doInvoke() { doInvoke }

    Object afterInvoke(Object object, String methodName, Object[] arguments, Object result) {
        result
    }

    protected abstract Class getAnnotation()
}
