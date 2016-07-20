package ee.cli.handler

import ee.cli.core.integ.JsonModelBasedHandler
import ee.cli.core.model.Command
import ee.cli.core.model.ModelDef

class DslHandler extends JsonModelBasedHandler {

    DslHandler() {
        classNameResolver = { String className -> "ee.cli.dsl.${className.capitalize()}Model" }
    }

    def resolveBase(def item, Command command) {
        if(ModelDef.isInstance(item)) {
            item.load()
        } else {
            super.resolveBase(item, command)
        }
    }
}