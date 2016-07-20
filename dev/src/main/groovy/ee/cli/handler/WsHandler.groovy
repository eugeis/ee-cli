package ee.cli.handler

import ee.cli.core.integ.JsonModelBasedHandler

class WsHandler extends JsonModelBasedHandler {

    WsHandler() {
        classNameResolver = { String className -> "ee.cli.ws.${className.capitalize()}Workspace" }
    }
}