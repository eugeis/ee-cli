package ee.cli.tool

import ee.cli.core.Valid
import ee.cli.core.integ.api.Runner
import ee.cli.core.integ.Injector
import ee.cli.core.integ.tool.BuildRequestImpl
import ee.cli.core.integ.tool.BuildTool
import ee.cli.core.model.BuildRequest
import ee.cli.core.model.ExecConfig
import ee.cli.core.model.Result
import ee.cli.core.model.Tool

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.TimeUnit

class GradleTool extends Tool implements BuildTool {
    @Inject
    Runner runner
    @Inject
    Injector injector
    File binDir
    String gradle

    @PostConstruct
    @Valid
    void init() {
        binDir = new File(home, 'bin')
        gradle = new File(binDir, 'gradle').canonicalPath
    }

    @Override
    BuildRequest buildRequest() {
        new BuildRequestImpl()
    }

    @Override
    boolean supports(File buildItemHome) {
        def file = new File(buildItemHome, 'build.gradle')
        file.exists()
    }

    @Override
    Result execute(File buildItemHome, BuildRequest buildRequest) {
        Result ret = new Result(context: [buildItemHome, buildRequest])
        if (supports(buildItemHome)) {
            if (BuildRequestImpl.isInstance(buildRequest)) {
                List<String> command = command(buildRequest)
                ExecConfig execConfig = injector.wire(new ExecConfig(home: buildItemHome, wait: true, cmd: command, log: log, timeout: 3, timeoutUnit: TimeUnit.HOURS))
                ret.ok = runner.run execConfig
            } else {
                ret.error = new IllegalArgumentException("Don't support the type of build request $buildRequest', please use 'buildtRequest()' method of the build tool.");
            }
        } else {
            ret.error = new IllegalArgumentException("The build item $buildItemHome is not supported.");
        }
        ret
    }

    protected List<String> command(BuildRequestImpl request) {
        List<String> merge = []
        merge << gradle

        List flags = new ArrayList(request.flags)
        List tasks = new ArrayList(request.tasks)

        if (!tasks.remove('test')) {
            flags << 'skipTests'
        }

        if (!tasks) {
            tasks << 'install'
        }

        merge.addAll(tasks)
        if (request.params) {
            merge.addAll(request.params.collect { k, v -> "-P$k=$v" })
        }
        if (flags) {
            merge.addAll(flags.collect { "-P$it" })
        }
        merge
    }
}