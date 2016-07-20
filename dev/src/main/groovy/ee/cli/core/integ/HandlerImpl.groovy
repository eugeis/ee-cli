package ee.cli.core.integ

import ee.cli.core.Handler
import ee.cli.core.model.Context
import ee.cli.core.model.Item
import ee.cli.core.model.Label
import ee.cli.core.model.Result

abstract class HandlerImpl extends Item implements Handler {

    Result execute(Context context, def source) {
        Result ret = null
        Deque params = context.commands
        if (params) {
            try {
                ret = doExecute(context, source)
            } catch (e) {
                ret = new Result(context: context, ok: false, error: e, failure: "Executing agains the $context failed, because of exception: $e")
                log.error ret, e
            }
        } else {
            ret = new Result(context: context, info: "No command to execute for $context triggered by $source.")
            log.warn ret
        }
        ret
    }

    protected abstract Result doExecute(Context context, def source)

    @Override
    protected Label label() {
        Label label = super.label()
        label.name = label.name.removeSuffix('Handler').uncapitalize()
        label
    }
}