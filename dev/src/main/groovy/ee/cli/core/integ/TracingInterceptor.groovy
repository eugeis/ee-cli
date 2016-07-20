package ee.cli.core.integ

import ee.cli.core.model.Base
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory

public class TracingInterceptor extends Base implements Interceptor {
    private static final IGNORE_METHODS = ['getClass', 'asBoolean', 'hashCode', 'equals', 'toString'] as Set
    private static final int MAX_LOGGER_ENTRIES = 10000
    private static final Map<String, XLogger> LOGGERS =
            Collections.synchronizedMap(new LinkedHashMap<String, XLogger>(MAX_LOGGER_ENTRIES + 1, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, XLogger> eldest) {
                    return size() > MAX_LOGGER_ENTRIES;
                }
            });

    private int indent = 0

    public Object beforeInvoke(Object object, String methodName, Object[] arguments) {
        if (!IGNORE_METHODS.contains(methodName)) {
            write(object, methodName, arguments, true)
            indent++
        }
        null
    }

    public Object afterInvoke(Object object, String methodName, Object[] arguments, Object result) {
        if (!IGNORE_METHODS.contains(methodName)) {
            indent--
            write(object, methodName, arguments, false, result)
        }
        result
    }

    public boolean doInvoke() {
        true
    }

    private String indent() {
        StringBuilder result = new StringBuilder()
        for (int i = 0; i < indent; i++) {
            result.append('  ')
        }
        result.toString()
    }

    protected void write(Object object, String methodName, Object[] arguments,
                         boolean entry, def result = null) {
        try {
            Class theClass = object instanceof Class ? (Class) object : object.class
            String className = theClass.name
            XLogger targetLog = findLogger(className)
            if (targetLog.isTraceEnabled()) {
                if (entry) {
                    targetLog.trace '{}({}){}->', methodName, buildArguments(arguments), indent()
                } else {
                    targetLog.trace '{}({}){}<-{}', methodName, buildArguments(arguments), indent(), result
                }
            }
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    protected String buildArguments(Object[] arguments) {
        arguments.collect { it.class.name }.join(',')
    }

    protected XLogger findLogger(String plainTargetClassName) {
        XLogger ret = LOGGERS.get(plainTargetClassName)
        if (ret == null) {
            ret = XLoggerFactory.getXLogger(plainTargetClassName)
            LOGGERS.put(plainTargetClassName, ret)
        }
        ret
    }
}