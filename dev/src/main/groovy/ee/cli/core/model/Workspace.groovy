package ee.cli.core.model

import ee.cli.core.Valid
import ee.cli.core.integ.Exists
import ee.cli.core.integ.ValidInterceptor

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class Workspace extends Item {
    @Inject
    @Exists
    File home
    File ee

    @PostConstruct
    @Valid
    void init() {
        ee = new File(home, '.ee')
    }

    boolean isValid() {
        home && home.exists()
    }

    String toString() {
        "Workspace[name: $home]"
    }
}
