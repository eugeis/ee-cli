package ee.cli.tool

import ee.cli.core.Valid
import ee.cli.core.integ.api.Runner
import ee.cli.core.model.ExecConfig
import ee.cli.core.model.Tool

import javax.annotation.PostConstruct
import javax.inject.Inject


class MySqlTool extends Tool {
    @Inject
    Runner runner
    File binDir

    @PostConstruct
    @Valid
    void init() {
        binDir = new File(home, 'bin')
    }

    @Valid
    def start(File defaultsFile, Map<String, Object> params = null) {
        if (defaultsFile?.exists()) {
            runner.run new ExecConfig(home: binDir, wait: false,
                    cmd: ['mysqld', "--defaults-file=${defaultsFile.canonicalPath}"], log: log)
        } else {
            log.error('Cannot start mysql, because the config file {} does not exists.', defaultsFile)
        }
    }

    def stop(String user, String password) {
        runner.run new ExecConfig(home: binDir,
                cmd: ['mysqladmin', "-u$user", "-p$password", 'shutdown'], log: log)
    }
}