package ee.cli.core.model

import ee.cli.core.integ.CommandFactory
import ee.cli.core.integ.HandlersHandler
import ee.cli.core.integ.Injector
import ee.cli.core.integ.TracingInterceptor

import javax.interceptor.Interceptors

@Interceptors([TracingInterceptor])
class Context extends Item {
    String[] args
    Deque commands
    Environment env
    Injector injector
    HandlersHandler handlersHandler
    CommandFactory commandFactory

    void attachCommands(String commandExpression, Command nextCommand, boolean onTop = false) {
        if (commandExpression) {
            addCommands(commandFactory.attach(commandExpression, nextCommand), onTop)
        }
    }

    void addCommands(String commandExpression, boolean onTop = false) {
        if (commandExpression) {
            addCommands(commandFactory.parse(commandExpression), onTop)
        }
    }

    void addCommands(List<Command> newCommands, boolean onTop = false) {
        if (newCommands) {
            if (onTop) {
                newCommands = newCommands.reverse()
                newCommands.each { this.commands.addFirst(it) }
            } else {
                this.commands.addAll(newCommands)
            }
        }
    }

    @Override
    public String toString() {
        "Context{${fillToString([]).join(',')}}"
    }

    public List<String> fillToString(List<String> fill) {
        if (commands) {
            fill << "params=$commands"
        }

        if (args) {
            fill << "args=$args"
        }
        fill
    }
}
