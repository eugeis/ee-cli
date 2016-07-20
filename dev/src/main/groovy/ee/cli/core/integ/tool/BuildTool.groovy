package ee.cli.core.integ.tool

import ee.cli.core.model.BuildRequest
import ee.cli.core.model.Result

interface BuildTool {
    BuildRequest buildRequest()
    boolean supports(File buildItemHome)
    Result execute(File buildItemHome, BuildRequest buildRequest)
}
