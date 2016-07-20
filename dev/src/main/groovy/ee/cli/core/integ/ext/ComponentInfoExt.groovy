package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension
import ee.cli.core.integ.ValidInterceptor
import ee.mdd.model.Element
import ee.mdd.util.ElementPrinter

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class ComponentInfoExt extends AbstractExtension {

    @Inject
    @NotNull
    ElementPrinter printer

    protected void doExtend() {

        Element.metaClass.show = {
            Element item = delegate
            printer.print(delegate)
        }

    }
}