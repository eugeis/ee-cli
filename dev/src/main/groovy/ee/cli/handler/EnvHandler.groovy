package ee.cli.handler

import ee.cli.core.integ.JsonModelBasedHandler
import ee.cli.core.model.Command
import ee.cli.core.model.Context
import ee.cli.core.model.Environment
import ee.cli.core.model.EnvironmentDelegate
import ee.cli.core.model.Result

class EnvHandler extends JsonModelBasedHandler {
    def ops = ['flags', 'params'] as Set

    EnvHandler() {
        classNameResolver = { String className -> "ee.cli.env.${className.capitalize()}Environment" }
    }

    @Override
    protected def resolveItem(Command command) {
        def ret = super.resolveItem(command)
        if (!ret) {
            ret = env
        }
        ret
    }

    void applyModelChanges(def item, boolean newItem = false) {
        if (item != env) {
            super.applyModelChanges(item, newItem)
        }
    }

    protected Result executeCallableOrNext(def itemOrThis, Command command, Context context, def source) {
        Result ret = new Result(context: context)
        Environment env = itemOrThis
        String[] keys = collectKeys(command)
        def base = env.settings(keys)

        Command currentCommand = command
        while (currentCommand) {
            if (base) {
                if (!currentCommand.params && base.hasProperty(currentCommand.callable)) {
                    base = executeGetProperty(base, currentCommand.callable)
                } else {
                    if ('flags' == currentCommand.callable && Map.isInstance(currentCommand.params)) {
                        currentCommand.params = currentCommand.params.keySet()
                    }
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

    protected String[] collectKeys(Command command) {
        def ret = []
        Command currentCommand = command
        while (currentCommand && !ops.contains(currentCommand.callable)) {
            ret << currentCommand.callable
            currentCommand = currentCommand.next
        }
        ret.toArray()
    }

    protected def doResolve(String itemName) {
        def ret = super.doResolve(itemName)
        if (ret && EnvironmentDelegate.isInstance(env) && env.target != ret) {
            env.target = ret
        }
        ret
    }
}