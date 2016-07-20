package ee.cli.core.integ

import ee.cli.core.Handler
import ee.cli.core.model.Command
import ee.cli.core.model.Context
import ee.cli.core.model.Result

import javax.inject.Inject

class HandlersHandler extends ClassBasedHandler {
    @Inject
    String defaultHandlerName = 'alias'
    Handler defaultHandler

    HandlersHandler() {
        classNameResolver = { String className -> "ee.cli.handler.${className.capitalize()}Handler" }
    }

    protected Result doExecute(Context context, source) {
        Result ret
        Command command = context.commands.peek()

        Object item

        if (command.item) {
            item = resolve(command.item)
        }

        if (item) {
            context.commands.poll()
            ret = execute(item, command, context, source)
        } else {
            if (!defaultHandler && defaultHandlerName) {
                defaultHandler = doResolve(defaultHandlerName)
            }
            ret = defaultHandler.execute(context, source)
        }
        ret
    }
}
