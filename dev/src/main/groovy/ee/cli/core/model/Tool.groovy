package ee.cli.core.model

import ee.cli.core.integ.Relative
import ee.cli.core.integ.ValidInterceptor

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class Tool extends Item {
    @Inject
    @NotNull
    String name
    @Inject
    @NotNull
    Workspace workspace
    @Inject
    @NotNull
    @Relative('workspace.home')
    File home
}
