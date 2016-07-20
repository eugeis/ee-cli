package ee.cli.handler

import ee.cli.core.integ.ItemBasedHandler
import ee.cli.core.integ.JsonModelBasedHandler
import ee.cli.core.model.*

import javax.inject.Inject
import javax.validation.constraints.NotNull

class AliasHandler extends JsonModelBasedHandler {
    @Inject
    @NotNull
    Workspace workspace

    @Inject
    List<String> itemsHandler = ['bundle', 'dsl', 'service']

    AliasHandler() {
        classNameResolver = { String className -> "ee.cli.alias.${className.capitalize()}Alias" }
    }

    protected Result execute(Alias item, Command command, Context context, def source) {
        item.apply(command, context)
        Result ret = new Result(context: context)
        ret
    }

    protected Result executeCallableOrNext(def itemOrThis, Command command, Context context, def source) {
        Result ret
        if (command.item) {
            Alias alias = findAndCreateHandlerAlias(context, command)
            if (alias) {
                ret = execute(alias, command, context, source)
            }
        } else {
            ret = super.executeCallableOrNext(itemOrThis, command, context, source)
        }
        ret
    }

    protected Alias findAndCreateHandlerAlias(Context context, Command command) {
        Alias ret
        for (String handlerName : itemsHandler) {
            ItemBasedHandler handler = context.handlersHandler.resolve(handlerName)
            if (handler) {
                def item = handler.resolve(command.item)
                if (item) {
                    ret = add([name: command.item, handler: handlerName, expression: command.item, automatic: true, type: 'handler'])
                    break
                }
            }
        }
        ret
    }
}