package ee.cli.core.model

import ee.cli.core.integ.Exists
import ee.cli.core.integ.Relative
import ee.cli.core.integ.ValidInterceptor
import ee.mdd.model.component.Facet
import ee.mdd.util.ElementPrinter

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class ModelDef extends Item {
    @Inject
    @NotNull
    String name
    @Inject
    @NotNull
    Workspace workspace
    @Inject
    @Exists
    @Relative('workspace.home')
    File file
    @Inject
    ElementPrinter printer
    Closure facet

    def load() {
        log.info "$this loaded."
    }

    def show() {
        def model = load()
        if (model) {
            printer.print(model, { !Facet.isInstance(it) })
        }
    }

    String toString() {
        name ?: label?.name
    }

}
