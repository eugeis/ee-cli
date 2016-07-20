package ee.cli.handler

import ee.cli.core.Handler
import ee.cli.core.model.Base
import ee.cli.core.model.Context
import ee.cli.core.model.Result

class TestHandler extends Base implements Handler {

    Result execute(Context context, def source) {
        log.info("Execute for $context triggered from $source")
        //all parameters handled
        context.commands.clear()
        new Result(info: 'Example implementation, just consume all parameters.')
    }

}