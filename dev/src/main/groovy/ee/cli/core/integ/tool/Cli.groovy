package ee.cli.core.integ.tool

import ee.cli.core.Valid
import ee.cli.core.integ.ValidInterceptor
import ee.cli.core.model.Base

import javax.interceptor.Interceptors

@Interceptors([ValidInterceptor])
class Cli extends Base {
    def silent = false

    def cli

    Cli() {
        //set location of jboss-cli.xml to avoid warning outputs
        System.setProperty('jboss.cli.config', 'jboss-cli.xml')
        cli = org.jboss.as.cli.scriptsupport.CLI.newInstance()
    }

    @Valid
    boolean isServerRunning() {
        serverState().asString() == 'running'
    }

    @Valid
    def serverState() {
        execute(':read-attribute(name=server-state)')
    }

    @Valid
    def execute(String cmdString) {
        if (!silent) {
            log.info 'Execute {}', cmdString
        }
        def result = cli.cmd(cmdString)
        result.response.result
    }

    @Valid
    def silent(def closureToExecute) {
        silent = true
        try {
            closureToExecute()
        } finally {
            silent = false
        }
    }

    def connect(String host, int port, String user, char[] password) {
        boolean ret = false
        try {
            cli.connect(host, port, user, password)
            ret = true
        } catch (e) {
            log.error('connect({}, {}, {}, ...)=>{}', host, port, user, e)
        }
        ret
    }

    void disconnect() {
        if(cli.commandContext && !cli.commandContext.terminated) {
            cli.disconnect()
        }
    }
}
