package ee.cli.core.integ

import ee.cli.core.Handler
import ee.cli.core.Valid
import ee.cli.core.model.Command
import ee.cli.core.model.Context
import ee.cli.core.model.Result

import javax.inject.Inject

abstract class ItemBasedHandler extends HandlerImpl implements Handler {
    @Inject
    Injector injector
    Map<String, Object> resolved = [:]


    @Valid
    Result execute(Context context, def source) {
        Result ret
        if (context.commands) {
            try {
                ret = doExecute(context, source)
            } catch (e) {
                ret = new Result(context: context, ok: false, error: e, failure: "Executing agains the $context failed, because of exception: $e")
                log.error ret, e
            }
        } else {
            ret = new Result(context: context, info: "No class defined to execute for $context triggered by $source.")
            log.warn ret
        }
        ret
    }

    protected Result doExecute(Context context, source) {
        Result ret
        Command command = context.commands.peek()

        Object item = resolveItem(command)

        if (item) {
            context.commands.poll()
            ret = execute(item, command, context, source)
        } else if (command.callable) {
            context.commands.poll()
            ret = executeCallableOrNext(this, command, context, source)
        } else {
            ret = new Result(context: context, ok: false, failure: "Cannot resolve item for '$command' agains the $context")
        }

        ret
    }

    protected Object resolveItem(Command command) {
        Object item

        if (command.item) {
            item = resolve(command.item)
        }
        item
    }

    protected void prepareFirstCommand(Command command) {
        if (!command.item && (command.callable && !command.params)) {
            command.item = command.callable
            command.callable = null
        }
    }

    def resolve(String itemName) {
        def ret

        if (resolved.containsKey(itemName)) {
            ret = resolved[itemName]
        } else {
            ret = doResolve(itemName)
            resolved[itemName] = ret
        }
        ret
    }

    protected Result execute(def item, Command command, Context context, def source) {
        Result ret
        if (command.callable) {
            ret = executeCallableOrNext(item, command, context, source)
        } else if (command.next) {
            ret = executeCallableOrNext(item, command.next, context, source)
        } else if (Handler.isInstance(item)) {
            ret = executeHandler(item, context, source)
        } else {
            ret = new Result(context: context, info: "No callable defined to call in '$item' for '$context' triggered by $source.")
        }
        ret
    }

    protected Result executeHandler(def item, Context context, def source) {
        item.execute(context, source)
    }

    protected Result executeCallableOrNext(def itemOrThis, Command command, Context context, def source) {
        Result ret = new Result(context: context)

        def base = resolveBase(itemOrThis, command)

        Command currentCommand = command
        while (currentCommand) {
            if (base) {
                if (!currentCommand.params && base.hasProperty(currentCommand.callable)) {
                    base = executeGetProperty(base, currentCommand.callable)
                } else {
                    base = executeCallMethod(base, currentCommand.callable, currentCommand.params)
                }
            } else {
                log.warn 'Base is null, but there is child command left {}', currentCommand
            }
            currentCommand = currentCommand.next
        }

        if (Result.isInstance(base)) {
            ret.results = []
            ret.results << base
            ret.ok = base.ok
        } else {
            ret.outcome = base
        }
        ret
    }

    def resolveBase(def item, Command command) {
        item
    }

    protected def executeCallMethod(item, String methodName, def params = null) {
        if (params && item.metaClass.respondsTo(item, methodName, params)) {
            log.debug 'Call method {}.{}({})', item, methodName, params
            item.metaClass.invokeMethod(item, methodName, params)
        } else if (item.metaClass.respondsTo(item, methodName)) {
            log.debug 'Call method {}.{}', item, methodName
            item."$methodName"()
        } else {
            log.warn 'There is no method or property with name {} in {}', methodName, item
        }
    }

    protected def executeGetProperty(item, String property) {
        log.debug 'Get property {}.{}', item, property
        item."$property"
    }

    protected abstract def doResolve(String itemName)
}