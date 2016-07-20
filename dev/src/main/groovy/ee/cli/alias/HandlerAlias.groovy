package ee.cli.alias

import ee.cli.core.model.Alias
import ee.cli.core.model.Command
import ee.cli.core.model.Context
import ee.cli.core.model.Result

import javax.inject.Inject

class HandlerAlias extends Alias {
    @Inject
    String handler
    @Inject
    String expression

    void apply(Command command, Context context) {
        if (command.callable) {
            context.attachCommands("$handler $expression",
                    new Command(callable: command.callable, params: command.params, next: command.next), true)
        } else if (command.next) {
            context.attachCommands("$handler $expression", command.next, true)
        } else {
            context.addCommands("$handler $expression", true)
        }
    }
}
