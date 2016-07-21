package ee.cli.core.integ.ext

import ee.cli.core.model.BuildRequest
import ee.cli.core.model.Result
import ee.mdd.model.component.Component
import ee.mdd.model.component.Model
import ee.mdd.model.component.Module

class ComponentAppSrvExt extends AbstractAppSrvExt {

    protected void doExtend() {

        AbstractCollection.metaClass.deploy = {
            deploy(delegate)
        }

        ArrayList.metaClass.deploy = {
            deploy(delegate)
        }

        Model.metaClass.deploy = {
            Model item = delegate
            deploy(item.findAllDown(Component))
        }

        Component.metaClass.deploy = {
            Component item = delegate
            deploy(item.findAllDown(Module))
        }

        Module.metaClass.deploy = {
            Module item = delegate
            if (deployable.contains(item.name)) {
                Component component = item.component()
                def modelPath = component.sourceHome.toPath().resolve(component.parent.artifact)
                def componentPath = modelPath.resolve(item.artifact)
                def earPath = componentPath.resolve('target').resolve("$item.artifact-${item.version}.$item.name")
                Result ret = new Result(context: earPath)
                ret.ok = appSrv.deploy(component.artifact, earPath)
                ret
            }
        }
    }
}