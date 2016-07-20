package ee.cli.core.integ.api

import ee.cli.core.Nop
import ee.cli.core.integ.NopInterceptor
import ee.cli.core.integ.RunnerException
import ee.cli.core.integ.StreamHandler
import ee.cli.core.model.ExecConfig
import ee.cli.core.model.Item

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.interceptor.Interceptors

@Interceptors([NopInterceptor])
class Runner extends Item {
    @Inject
    Os os

    boolean windows
    String shell
    String inShellOption
    String noWaitNoConsoleOption

    @PostConstruct
    void init() {
        windows = os.isWindows()
        shell = windows ? 'cmd' : 'sh'
        inShellOption = windows ? '/c' : '-c'
        noWaitNoConsoleOption = windows ? 'start' : ''
    }

    @Nop
    boolean run(ExecConfig execConf) {
        def exitCode = 0
        log.info 'run {}', execConf
        def processBuilder = new ProcessBuilder(buildCommandChain(execConf)).directory(execConf.home).redirectErrorStream(true)

        Map<String, String> env = processBuilder.environment()
        execConf.env.each { String key, def value ->
            env.put(key, String.valueOf(value))
        }
        Process process = processBuilder.start()

        if (execConf.wait) {
            StreamHandler intputStreamHandler =
                    new StreamHandler(process.getInputStream(), execConf.filter, execConf.filterPattern,
                            execConf.log ?: log, execConf.outputProcessor)
            intputStreamHandler.start()

            if (!process.waitFor(execConf.timeout, execConf.timeoutUnit)) {
                log.warn('Timeout reached {} {}', execConf.timeout, execConf.timeoutUnit)
                exitCode = -1
            } else {
                exitCode = process.exitValue()
            }
            if (execConf.failOnError && (exitCode != 0)) {
                throw new RunnerException(exitCode)
            }
        }
        exitCode == 0
    }

    List<String> buildCommandChain(ExecConfig execConf) {
        List<String> ret = []
        ret << shell << inShellOption

        if (!execConf.wait && !execConf.noConsole) {
            ret << noWaitNoConsoleOption
        }

        if (windows) {
            // we have to do this hack here because the cmd may be specified as array to support commands like "mysql -e 'source file'" where
            // 'source file' must not be split
            if (execConf.cmd instanceof Collection) {
                execConf.cmd.each { String part -> ret << part }
            } else {
                execConf.cmd.split(' ').each { String part -> ret << part }
            }

        } else {
            if (execConf.cmd instanceof Collection) {
                String cmd = execConf.cmd.join(' ')
                ret << cmd

            } else {
                String cmd = execConf.cmd
                ret << cmd
            }
        }
        ret
    }
}
