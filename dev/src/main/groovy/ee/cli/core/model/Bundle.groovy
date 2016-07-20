package ee.cli.core.model

import ee.cli.core.integ.ValidInterceptor

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
abstract class Bundle extends Item {
    @Inject
    @NotNull
    String name

    def load() {
        log.info "$this loaded."
    }

    def show() {
        println("name: $name, items: ${items()}")
    }


    abstract List<Object> items()
}
