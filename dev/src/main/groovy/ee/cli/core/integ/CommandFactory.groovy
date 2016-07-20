package ee.cli.core.integ

import ee.cli.core.integ.api.Loader
import ee.cli.core.model.Command

import javax.inject.Inject
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by z000ru5y on 03.06.2016.
 */
class CommandFactory {
    //{item}.{callable}({params})
    static Pattern commandPattern = Pattern.compile('(( )+?(?<item>[0-9a-zA-Z_-]+)\\.)?((?<callable>[0-9a-zA-Z_-]+)(?<params>( )*(\\(|\\{)(.+?)( )*(\\)|\\}))?(\\.)?)?')

    @Inject
    Loader loader

    public List<Command> attach(String commandExpression, Command nextCommand) {
        List<Command> ret = parse(commandExpression)
        if (ret) {
            def lastCommand = ret.get(ret.size() - 1)
            while(lastCommand.next) {
                lastCommand = lastCommand.next
            }
            if (!lastCommand.callable && !lastCommand.params && !lastCommand.next) {
                lastCommand.callable = nextCommand.callable
                lastCommand.params = nextCommand.params
                lastCommand.next = nextCommand.next
            } else {
                lastCommand.next = nextCommand
            }
        } else {
            ret << nextCommand
        }
        ret
    }


    public List<Command> parse(String commandExpression) {
        List<Command> ret = []
        Matcher commandMatcher = commandPattern.matcher(commandExpression)
        Command parent
        while (commandMatcher.find()) {
            Command command = new Command()
            command.item = commandMatcher.group('item')
            command.callable = commandMatcher.group('callable')
            def params = commandMatcher.group('params')?.trim()

            fillParams(params, command)

            //is command not empty?
            if (command.item || command.callable || command.params) {
                //new item or first command
                if (command.item || !parent) {
                    ret << command
                    if (!command.item && !command.params) {
                        command.item = command.callable
                        command.callable = null
                    }
                    parent = command
                } else if (!parent.callable && !parent.params) {
                    parent.callable = command.callable
                    parent.params = command.params
                } else {
                    parent.next = command
                    parent = command
                }
            } else {
                parent = null
            }
        }
        ret
    }

    protected void fillParams(String params, Command command) {
        if (params) {
            if (params.startsWith('(')) {
                boolean map = params.contains(':') && !params.contains(':/') && !params.contains(':\\')
                String[] paramParts = params.substring(1, params.length() - 1).split(',')
                if (paramParts) {
                    if (map) {
                        command.params = [:]
                        for (String part : paramParts) {
                            part = part.trim()
                            int sepPos = part.indexOf(':')
                            if (sepPos >= 0) {
                                command.params[part.substring(0, sepPos).trim()] = part.substring(sepPos + 1).trim()
                            } else {
                                command.params[part] = ''
                            }
                        }
                    } else {
                        command.params = paramParts.collect { it.trim() }.toArray()
                    }
                }
            } else if (params.startsWith('{')) {
                Closure closure = loader.parseClosure(params)
                command.params = closure ?: params
            }
        }
    }
}
