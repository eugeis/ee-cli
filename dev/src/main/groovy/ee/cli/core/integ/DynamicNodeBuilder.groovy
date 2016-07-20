package ee.cli.core.integ

class DynamicNodeBuilder extends NodeBuilder {
    protected Map<String, Object> nameToNode = [:]
    def methodMissing(String name, def args) {
        def ret = createNode(name, args)
        nameToNode[name] = ret
        ret
    }

    def propertyMissing(String name) {
        def ret
        if(nameToNode.containsKey(name)) {
            ret = nameToNode[name]
        } else {
            ret = createNode(name)
            nameToNode[name] = ret
        }
        ret
    }

    def propertyMissing(String name, def arg) {
        def ret = createNode(name, arg)
        nameToNode[name] = ret
        ret
    }
}
