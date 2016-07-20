package ee.cli.core.model

class DynamicItem extends Item {
    protected Map<String, Object> props = [:]

    def methodMissing(String name, def args) {
        def ret = new DynamicItem(name: name)
        if (props.containsKey(name)) {
            def list = props[name]
            if (!Collection.isInstance(list)) {
                props[name] = [list, ret]
            } else {
                list << ret
            }
        } else {
            props[name] = ret
        }

        args.each { def arg ->
            if (Closure.isInstance(arg)) {
                Closure closure = arg
                closure.delegate = ret
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } else if (Map.isInstance(arg)) {
                arg.each { String k, v ->
                    ret[k] = v
                }
            } else {
                log.warn('Ignore arg {}, it is not supported yet.', arg)
            }
        }
        ret
    }

    def propertyMissing(String name) {
        def ret
        if (props.containsKey(name)) {
            ret = props[name]
        } else {
            ret = new DynamicItem(name: name)
            props[name] = ret
        }
        ret
    }

    def propertyMissing(String name, def arg) {
        props[name] = arg
    }

    boolean isEmpty() {
        props.isEmpty()
    }

    @Override
    public String toString() {
        props.name ?: super.toString()
    }

    def contains(String key) {
        props.containsKey(key)
    }
}
