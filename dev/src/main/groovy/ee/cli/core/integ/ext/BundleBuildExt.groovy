package ee.cli.core.integ.ext

import ee.cli.core.model.BuildRequest
import ee.cli.core.model.Bundle

class BundleBuildExt extends AbstractBuildExt {

    protected void doExtend() {

        Bundle.metaClass.build = {
            Bundle item = delegate
            item.build(buildRequest())
        }

        Bundle.metaClass.build = { List<String> tasks ->
            Bundle item = delegate
            BuildRequest request = buildRequest(tasks)
            item.build(request)
        }

        Bundle.metaClass.build = { Map<String, Object> params ->
            Bundle item = delegate
            BuildRequest request = buildRequest(params)
            item.build(request)
        }

        Bundle.metaClass.build = { BuildRequest request ->
            Bundle item = delegate
            build(item.items(), request)
        }
    }
}