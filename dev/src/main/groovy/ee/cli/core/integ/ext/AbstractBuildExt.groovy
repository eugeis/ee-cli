package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension
import ee.cli.core.integ.ValidInterceptor
import ee.cli.core.integ.tool.BuildToolImpl
import ee.cli.core.model.BuildRequest
import ee.cli.core.model.Result

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
abstract class AbstractBuildExt extends AbstractItemsExt {

    @Inject
    @NotNull
    BuildToolImpl buildTool

    protected Result deploy(List items, BuildRequest request) {
        Result ret = execute(items, { it.build(request) })
        ret
    }

    protected Result build(BuildRequest request, File buildItemHome) {
        Result ret
        if (buildTool.supports(buildItemHome)) {
            log.info "build {}", buildItemHome
            ret = buildTool.execute(buildItemHome, request)
        } else {
            ret = new Result(ok: false,
                    context: buildItemHome,
                    info: "The item $buildItemHome is not supported whether by Maven nor by Gradle.")
        }
        ret
    }

    protected BuildRequest buildRequest(Map<String, Object> params = null) {
        BuildRequest ret = buildTool.buildRequest()
        params?.each { k, v ->
            if (v) {
                ret.param(k, v)
            } else if (k) {
                ret.task(k)
            }
        }
        ret
    }
}