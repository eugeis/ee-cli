package ee.cli.service

import ee.cli.core.Valid
import ee.cli.core.integ.Export
import ee.cli.core.integ.Relative
import ee.cli.core.integ.api.Io
import ee.cli.tool.WildFlyTool

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.validation.constraints.NotNull
import java.nio.file.Path
import java.nio.file.Paths

class WildFlyService extends SocketService {
    @Inject
    @NotNull
    WildFlyTool tool
    @Inject
    Io io
    @Inject
    @Export
    @Relative('tool.home')
    File home
    @Inject
    @NotNull
    @Relative
    File configFile
    @Inject
    @NotNull
    @Export
    Integer managementPort = 9990
    @Inject
    @NotNull
    String managementUser = 'admin'
    @Inject
    @NotNull
    String managementPassword = 'admin'

    @PostConstruct
    void init() {
        super.init()
    }

    @Valid
    def start(Map<String, Object> params = null) {
        def dbParams = params?.findAll { k, v -> k.startsWith('db.') }
        if (dbParams) {
            //remove after refactoring WildFly start parameters
            String address = "${dbParams['db.host']}:${dbParams['db.port']}"
            params['database.serv1.address'] = address
            params['database.serv2.address'] = address
            params['database.addresses'] = address
        }
        tool.start configFile, host, port, params
    }

    @Valid
    def stop() {
        tool.stop(host, managementPort, managementUser, managementPassword)
    }

    @Valid
    def deploy(String appName, String appFilePath) {
        deploy(appName, appFilePath.toPath())
    }

    @Valid
    def deploy(String appName, Path appFilePath) {
        Path deployedSymLink = home.toPath().resolve('deployments').resolve("$appName.${appFilePath.ext()}")
        io.createSymLink(deployedSymLink, appFilePath)
    }

    @Valid
    protected int getDefaultPort() {
        8080
    }

    @Valid
    List<Path> collectLogs() {
        [home.toPath().resolve('log')]
    }

    @Valid
    List<Path> collectConfigs() {
        [configFile.parentFile.toPath()]
    }

}
