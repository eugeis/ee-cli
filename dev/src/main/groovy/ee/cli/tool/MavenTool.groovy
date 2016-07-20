package ee.cli.tool

import ee.cli.core.Valid
import ee.cli.core.integ.api.Runner
import ee.cli.core.integ.Injector
import ee.cli.core.integ.tool.BuildRequestImpl
import ee.cli.core.integ.tool.BuildTool
import ee.cli.core.model.*

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.TimeUnit

class MavenTool extends Tool implements BuildTool {
    @Inject
    Runner runner
    @Inject
    Security security
    @Inject
    Injector injector
    File binDir
    String mvn
    @Inject
    String defaultCommandSuffix = ""
    @Inject
    Map defaultParams = [:]
    @Inject
    List defaultFlags = []
    @Inject
    List defaultProfiles = []

    @PostConstruct
    @Valid
    void init() {
        binDir = new File(home, 'bin')
        mvn = new File(binDir, 'mvn').canonicalPath
    }

    @Override
    BuildRequest buildRequest() {
        new BuildRequestImpl()
    }

    @Override
    boolean supports(File buildItemHome) {
        def file = new File(buildItemHome, 'pom.xml')
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
                ret.error = new IllegalArgumentException("Don't support the type of build request $buildRequest', please use 'buildRequest()' method of the build tool $this.");
            }
        } else {
            ret.error = new IllegalArgumentException("The build item $buildItemHome is not supported.");
        }
        ret
    }

    protected List<String> command(BuildRequestImpl request) {
        List<String> ret = []
        ret << mvn

        List flags = new ArrayList(request.flags)
        List tasks = new ArrayList(request.tasks)

        if (!tasks) {
            tasks << 'install'
        }

        if (!tasks.remove('test')) {
            flags << 'skipTests'
        }

        ret.addAll(tasks)
        fillParams(defaultParams, ret)
        fillParams(request.params, ret)
        fillFlags(defaultFlags, ret)
        fillFlags(flags, ret)
        fillProfiles(defaultProfiles, ret)

        if (defaultCommandSuffix) {
            ret << defaultCommandSuffix
        }
        ret
    }

    protected void fillParams(Map items, ArrayList<String> ret) {
        if (items) {
            ret.addAll(items.collect { k, v -> "-D$k=$v" })
        }
    }

    protected void fillFlags(List items, List<String> fill) {
        if (items) {
            fill.addAll(items.collect { "-D$it" })
        }
    }

    protected void fillProfiles(List items, List<String> fill) {
        if (items) {
            fill.addAll(items.collect { "-P$it" })
        }
    }
}