package ee.cli.service

import ee.cli.core.Valid
import ee.cli.core.integ.api.Runner
import ee.cli.core.integ.Injector
import ee.cli.core.integ.Relative
import ee.cli.core.model.ExecConfig

import javax.inject.Inject
import java.nio.file.Path

class JavaService extends JmxService {
    @Inject
    Injector injector
    @Inject
    Runner runner
    @Inject
    @Relative('workspace.home')
    File home
    @Inject
    @Relative
    File logDir
    @Inject
    @Relative
    File configDir
    @Inject
    String command

    @Override
    def start() {
        ExecConfig execConfig = injector.wire(new ExecConfig(home: home, wait: true, cmd: command))
        runner.run execConfig
    }

    @Valid
    List<Path> collectLogs() {
        logDir ? [logDir.toPath()] : Collections.emptyList()
    }

    @Valid
    List<Path> collectConfigs() {
        configDir ? [configDir.toPath()] : Collections.emptyList()
    }
}
