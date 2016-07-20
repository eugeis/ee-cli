package ee.cli.core.model

import ee.cli.core.integ.ValidInterceptor

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull
import java.nio.file.Path

@Interceptors([ValidInterceptor])
class Service extends Item {
    @Inject
    @NotNull
    String name
    @Inject
    String category //e.g. db, as, ...
    @Inject
    @NotNull
    Workspace workspace
    @Inject
    List<String> dependsOn
    @Inject
    List<String> dependsOnMe

    def start(Map<String, Object> params = null) {
        log.info "$this stared."
    }

    def stop() {
        log.info "$this stopped."
    }

    def ping() {
        false
    }

    List<Path> collectLogs() {
    }

    List<Path> collectConfigs() {
    }

    void copyLogs(String target) {
        copyLogs(target.toPath())
    }

    void copyConfigs(String target) {
        copyConfigs(target.toPath())
    }

    void copyLogs(Path target) {
        Path targetForService = target.resolve(name)
        collectLogs().each { it.copyTo(targetForService) }
    }

    void copyConfigs(Path target) {
        Path targetForService = target.resolve(name)
        collectConfigs().each { it.copyTo(targetForService) }
    }

    String toString() {
        name ?: label?.name
    }
}
