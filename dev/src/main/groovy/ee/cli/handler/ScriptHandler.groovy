package ee.cli.handler

import ee.cli.core.integ.JsonModelBasedHandler
import ee.cli.core.model.Workspace

import javax.inject.Inject
import javax.validation.constraints.NotNull

class ScriptHandler extends JsonModelBasedHandler {
    @Inject
    @NotNull
    Workspace workspace

    ScriptHandler() {
        classNameResolver = { String className -> "ee.cli.alias.${className.capitalize()}Task" }
    }
}