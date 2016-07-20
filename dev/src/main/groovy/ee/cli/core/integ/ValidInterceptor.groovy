package ee.cli.core.integ

import ee.cli.core.Valid

import javax.annotation.PostConstruct
import javax.validation.constraints.NotNull
import java.lang.reflect.Field

class ValidInterceptor extends AnnotationBasedInterceptor implements Interceptor {
    Closure validator

    @PostConstruct
    void init() {
        super.init()
        def notNullProps = clazz.findAnnotatedFields(targetClass, NotNull)?.collect { it.name } as Set
        def existsProps = clazz.findAnnotatedFields(targetClass, Exists)?.collect { it.name } as Set
        if (notNullProps || existsProps) {
            validator = { instance ->
                StringBuffer ret = new StringBuffer()

                String nextSep = ''
                if (notNullProps) {
                    notNullProps.each { String name ->
                        def value = instance."$name"
                        if (!value) {
                            ret.append(nextSep).append(name).append('=').append(value)
                            nextSep = ','
                        }
                    }
                }

                if (existsProps) {
                    existsProps.each { String name ->
                        def value = instance."$name"
                        if (!value) {
                            ret.append(nextSep).append(name).append('=').append(value)
                            nextSep = ','
                        } else {
                            if (File.isInstance(value) && !((File) value).exists()) {
                                ret.append(nextSep).append(name).append('=').append(value).append("->does not exists.")
                                nextSep = ','
                            }
                        }
                    }
                }
                ret
            }
        }
    }

    Object beforeInvoke(Object obj, String methodName, Object[] args) {
        def ret
        doInvoke = true
        if (isMethodAnnotated(methodName, args)) {
            doInvoke = isValid(obj, methodName, args)
            if (!doInvoke && methodName.equalsIgnoreCase('asBoolean')) {
                ret = false
            }
        }
        ret
    }

    protected boolean isValid(obj, String methodName, Object[] args) {
        boolean ret = true
        if (validator) {
            String notValidCause = validator(obj)
            if (notValidCause) {
                log.warn '(notValid)-> {}.{}({}) => {}', obj, methodName, args, notValidCause
                ret = false
            }
        }
        ret
    }

    @Override
    protected Class getAnnotation() {
        Valid
    }
}
