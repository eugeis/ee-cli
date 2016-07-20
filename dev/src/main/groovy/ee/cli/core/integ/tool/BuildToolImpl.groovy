package ee.cli.core.integ.tool

import ee.cli.core.integ.TracingInterceptor
import ee.cli.core.model.Base
import ee.cli.core.model.BuildRequest
import ee.cli.core.model.Result
import ee.cli.tool.GradleTool
import ee.cli.tool.MavenTool

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([TracingInterceptor])
class BuildToolImpl extends Base implements BuildTool {
    @Inject
    @NotNull
    MavenTool mvn
    @Inject
    @NotNull
    GradleTool gradle

    @Override
    boolean supports(File buildItemHome) {
        mvn.supports(buildItemHome) || gradle.supports(buildItemHome)
    }

    @Override
    BuildRequest buildRequest() {
        new BuildRequestImpl()
    }

    @Override
    Result execute(File buildItemHome, BuildRequest buildRequest) {
        Result ret
        if (mvn.supports(buildItemHome)) {
            ret = mvn.execute(buildItemHome, buildRequest)
        } else if (gradle.supports(buildItemHome)) {
            ret = gradle.execute(buildItemHome, buildRequest)
        } else {
            ret.ok = false
            ret.info = "Skip $buildItemHome because the item is not buildable."
        }
        ret
    }
}
