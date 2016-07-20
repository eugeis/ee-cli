package ee.cli.tool

import ee.cli.core.Valid
import ee.cli.core.integ.api.Net
import ee.cli.core.integ.api.Runner
import ee.cli.core.model.ExecConfig
import ee.cli.core.model.Tool

import javax.annotation.PostConstruct
import javax.inject.Inject

class WildFlyTool extends Tool {
    @Inject
    Runner runner
    @Inject
    Net net
    File binDir

    @PostConstruct
    @Valid
    void init() {
        binDir = new File(home, 'bin')
    }

    @Valid
    def start(File configFile, String host, int port, Map<String, Object> params = null) {
        if (configFile?.exists()) {
            def cmd = ['standalone', "-b=$host", "-bmanagement=$host",
                       "-Djboss.home.dir=$home", "-Djboss.server.config.dir=$configFile.parentFile.canonicalPath"]
            if (params) {
                params.each { k, v -> cmd << "-D$k=$v" }
            }
            cmd << '-c'
            cmd << configFile.name

            runner.run new ExecConfig(home: binDir, env: ['NOPAUSE': true], wait: false, cmd: cmd, log: log)
            net.waitForPortOpen(host, port, name)
        } else {
            log.error('Cannot start WildFly, because the config file {} does not exists.', configFile)
        }
    }

    def stop(String host, int managementPort, String managementUser, String managementPassword) {
        runner.run new ExecConfig(home: binDir, env: ['NOPAUSE': true],
                cmd: ['jboss-cli', '--connect', "--controller=$host:$managementPort",
                      "--user=$managementUser", "--password=$managementPassword", 'shutdown'], log: log)
        net.waitForPortClose(host, managementPort, name)
    }
}