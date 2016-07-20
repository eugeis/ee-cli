package ee.cli.core.integ.api

import ee.cli.core.model.Item
import sun.reflect.Reflection

import javax.interceptor.Interceptors
import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Method

class Clazz extends Item {
    protected Map<Object, List<Field>> keyToAnnotatedFields = new HashMap<>();
    protected Map<Object, List<Method>> keyToAnnotatedMethods = new HashMap<>();

    List<Field> findAnnotatedFields(Class clazz, Class... annotations) {
        def key = Arrays.hashCode(annotations) + clazz.hashCode()
        def ret = keyToAnnotatedFields.get(key)
        if (ret == null) {
            ret = []
            keyToAnnotatedFields.put(key, ret)
            for (Field item : findAllFields(clazz)) {
                for (Class<? extends Annotation> annotation : annotations) {
                    if (item.isAnnotationPresent(annotation)) {
                        ret.add(item)
                        break
                    }
                }
            }
        }
        ret
    }

    List<Method> findAnnotatedMethods(Class clazz, Class... annotations) {
        def key = Arrays.hashCode(annotations) + clazz.hashCode()
        def ret = keyToAnnotatedMethods.get(key)
        if (ret == null) {
            ret = []
            keyToAnnotatedMethods.put(key, ret)
            for (Method item : findAllMethods(clazz)) {
                for (Class<? extends Annotation> annotation : annotations) {
                    if (item.isAnnotationPresent(annotation)) {
                        ret.add(item)
                        break
                    }
                }
            }
        }
        ret
    }

    List<Field> findAllFields(Class clazz) {
        def ret = []
        if (clazz != null) {
            if (clazz.superclass) {
                ret.addAll(findAllFields(clazz.superclass))
            }
            ret.addAll(clazz.declaredFields)
        }
        ret
    }

    List<Method> findAllMethods(Class clazz) {
        def ret = []
        if (clazz != null) {
            if (clazz.superclass) {
                ret.addAll(findAllMethods(clazz.superclass))
            }
            ret.addAll(clazz.declaredMethods)
        }
        ret
    }

    List<Class> findInterceptors(Class clazz) {
        def ret = []
        if (clazz != null) {
            if (clazz.superclass) {
                ret.addAll(findInterceptors(clazz.superclass))
            }
            Annotation annotation = clazz.getAnnotations().find { it.annotationType() == Interceptors }
            if (annotation) {
                ret.addAll annotation.value()
            }
        }
        ret = ret.unique()
        ret
    }

    GroovyClassLoader findGroovyClassLoader() {
        GroovyClassLoader ret
        int frame = 1 // number of levels above you in the stack
        Class c = Reflection.getCallerClass(frame)
        while (c != null && !(c.getClassLoader() instanceof GroovyClassLoader)) {
            frame++;
            c = Reflection.getCallerClass(frame);
        }
        if (c != null) {
            ret = (GroovyClassLoader) c.classLoader
        }
        ret
    }
}
