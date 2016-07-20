package ee.cli.core.model

import ee.cli.core.Valid
import ee.cli.core.integ.api.Ant
import ee.cli.core.integ.Relative
import ee.cli.core.integ.ValidInterceptor
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

import static ee.cli.core.model.PackageState.*

@Interceptors([ValidInterceptor])
class Package extends Item {
    @Inject
    @NotNull
    String name
    @Inject
    String category
    @Inject
    @NotNull
    MavenCoordinate coordinate
    @Inject
    @NotNull
    PackageState state = UNKNOWN
    @Inject
    @NotNull
    Workspace workspace
    @Inject
    File resolvedFile
    @Inject
    @Relative('workspace.ee')
    File preparedFile
    @Inject
    @Relative('workspace.home')
    File installedFile
    @Inject
    List<String> dependsOn
    @Inject
    List<String> dependsOnMe

    File preparedDir

    @Inject
    @NotNull
    Ant ant

    @PostConstruct
    @Valid
    void init() {
        preparedDir = new File(workspace.ee, 'prepared')
    }

    @Valid
    def prepare(Map<String, Object> params = null) {
        state = PREPARED
        log.info "$name prepared."
    }

    def configure(Map<String, Object> params = null) {
        log.info "$name configured."
    }

    @Valid
    def install(Map<String, Object> params = null) {
        state = INSTALLED
        log.info "$name installed."
    }

    @Valid
    def uninstall() {

        if (installedFile?.exists()) {
            if (installedFile.isDirectory()) {
                installedFile.deleteDir()
            } else {
                installedFile.delete()
            }
        }

        if (preparedFile?.exists()) {
            preparedFile.delete()
        }
        state = PackageState.UNINSTALLED
        log.info "$name uninstalled."
    }

    @Override
    protected Label label() {
        Label label = super.label()
        label.name = label.name.removeSuffix('Package').uncapitalize()
        label
    }

    String toString() {
        name ?: label?.name
    }
}
