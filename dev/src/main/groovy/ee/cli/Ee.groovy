package ee.cli

import ee.cli.core.ContextFactory
import ee.cli.core.StaticExtensions
import ee.cli.core.model.Base
import ee.cli.core.model.Context
import ee.cli.core.model.Label
import ee.cli.core.model.Result

class Ee extends Base {
    static {
        println ' === ee === '
        new StaticExtensions().extend()
    }

    Label label = this.buildLabel()

    Result execute(String[] args) {
        Date started = new Date()
        Context context = new ContextFactory().parse(args)
        Result ret
        if (context.commands) {
            while (context.commands) {
                Deque prev = context.commands.clone()
                Result result = context.handlersHandler.execute(context, label)
                if (!ret) {
                    ret = result
                } else {
                    if(!ret.results) {
                        ret.results = []
                    }
                    ret.results << result
                    if (ret.ok && !result.ok) {
                        ret.ok = result.ok
                    }
                }
                if (prev.size() == context.commands.size() && context.commands.containsAll(prev)) {
                    log.info 'Stop the execution because the chain {} is unchanged after last execution.', context.commands
                    break
                }
            }
        } else {
            ret = new Result(info: "Nothing to execute for $context")
            help()
        }
        log.info ret.toStringDetails()
        if (!ret.ok) {
            log.warn ret.toStringDetails(true)
        }

        Date ended = new Date()
        log.info 'Started at {} ended at {} and ran for {} ms.',
                started.longTime(), ended.longTime(), ended.time - started.time
        ret
    }

    void help() {
    }
}