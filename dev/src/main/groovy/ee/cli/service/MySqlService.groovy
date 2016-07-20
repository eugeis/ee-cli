package ee.cli.service

import ee.cli.core.Valid
import ee.cli.core.integ.Relative
import ee.cli.tool.MySqlTool

import javax.inject.Inject
import javax.validation.constraints.NotNull
import java.nio.file.Path

class MySqlService extends SocketService {
    @Inject
    @NotNull
    MySqlTool tool
    @Inject
    @Relative('workspace.home')
    File home
    @Inject
    @NotNull
    @Relative
    File configFile
    @Inject
    @NotNull
    String defaultUser = 'root'
    @Inject
    @NotNull
    String defaultPassword = 'admin'

    @Valid
    def start(Map<String, Object> params = null) {
        tool.start configFile, params
    }

    @Valid
    def stop() {
        tool.stop defaultUser, defaultPassword
    }

    @Valid
    protected int getDefaultPort() {
        3306
    }

    @Valid
    List<Path> collectLogs() {
        List<Path> ret = []
        configFile.parentFile.eachFile {
            if (it.name.endsWith('.log') || it.name.endsWith('.err')) {
                ret << it.toPath()
            }
        }
        ret
    }

    @Valid
    List<File> collectConfigs() {
        [configFile]
    }
}