package ee.cli.core.model

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate

import javax.inject.Inject
import javax.validation.constraints.NotNull

class AddOn extends Item {
    @Inject
    @NotNull
    MavenCoordinate coordinate


}
