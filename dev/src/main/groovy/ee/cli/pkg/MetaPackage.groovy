package ee.cli.pkg

import ee.cli.core.Valid
import ee.cli.core.model.Package
import ee.cli.core.model.PackageState
import ee.cli.handler.PkgHandler
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate

import javax.inject.Inject
import javax.validation.constraints.NotNull

class MetaPackage extends ContentPackage {
    @Inject
    @NotNull
    MavenCoordinate targetCoordinate

    @Inject
    PkgHandler pkgHandler

    @Valid
    def prepare(Map<String, Object> params = null) {
        super.prepare(params)
    }

    @Valid
    def uninstall() {
        Package targetPackage = pkgHandler.resolve(targetCoordinate.toCanonicalForm())
        if (targetPackage && targetPackage.state.installed) {
            targetPackage.state = PackageState.UNINSTALLED
            pkgHandler.applyModelChanges(targetPackage)
        }
        super.uninstall()
    }
}
