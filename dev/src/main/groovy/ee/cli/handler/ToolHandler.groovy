package ee.cli.handler

import ee.cli.core.integ.JsonModelBasedHandler

class ToolHandler extends JsonModelBasedHandler {

    ToolHandler() {
        classNameResolver = { String className -> "ee.cli.tool.${className.capitalize()}Tool" }
    }
}