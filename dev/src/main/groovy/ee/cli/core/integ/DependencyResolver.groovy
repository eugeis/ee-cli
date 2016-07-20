package ee.cli.core.integ

import ee.cli.core.model.Workspace
import org.jboss.shrinkwrap.resolver.api.maven.Maven
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class DependencyResolver {
    @Inject
    @NotNull
    Workspace workspace

    @PostConstruct
    void init() {


    }

    MavenResolvedArtifact[] resolve(String canonicalForm) {
        def ret = Maven.resolver().resolve(canonicalForm).withTransitivity().asResolvedArtifact()
        ret
    }

    MavenResolvedArtifact[] resolve(MavenCoordinate coordinate) {
        def ret = resolve(coordinate.toCanonicalForm())
        ret
    }

    File getRepoDir() {
        new File(workspace.eeDir, 'repo')
    }
}
