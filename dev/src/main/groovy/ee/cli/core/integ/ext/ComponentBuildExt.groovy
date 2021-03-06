package ee.cli.core.integ.ext

import ee.cli.core.model.BuildRequest
import ee.mdd.model.component.*

class ComponentBuildExt extends AbstractBuildExt {

    protected void doExtend() {

        AbstractCollection.metaClass.build = {
            build(delegate, buildRequest())
        }

        AbstractCollection.metaClass.build = { Map<String, Object> params ->
            BuildRequest request = buildRequest(params)
            build(delegate, request)
        }

        ArrayList.metaClass.build = {
            build(delegate, buildRequest())
        }

        ArrayList.metaClass.build = { Map<String, Object> params ->
            BuildRequest request = buildRequest(params)
            build(delegate, request)
        }

        StructureUnit.metaClass.build = {
            StructureUnit item = delegate
            item.build(buildRequest())
        }

        StructureUnit.metaClass.build = { Map<String, Object> params ->
            StructureUnit item = delegate
            BuildRequest request = buildRequest(params)
            item.build(request)
        }

        StructureUnit.metaClass.build = { List<String> tasks ->
            StructureUnit item = delegate
            BuildRequest request = buildRequest(tasks)
            item.build(request)
        }

        Model.metaClass.build = { BuildRequest request ->
            Model item = delegate
            build(item.findAllDown(ModuleGroup), request)
        }

        Component.metaClass.build = { BuildRequest request ->
            Component item = delegate
            build(item.findAllDown(ModuleGroup), request)
        }

        ModuleGroup.metaClass.build = { BuildRequest request ->
            ModuleGroup item = delegate
            Component component = item.component()
            def modelFolder = new File(component.sourceHome, component.parent.artifact)
            def buildItemHome = new File(modelFolder, "$component.artifact-root/release-units/$item.name")
            build(request, buildItemHome)
        }

        Module.metaClass.build = { BuildRequest request ->
            Module item = delegate
            Component component = item.component()
            def modelFolder = new File(component.sourceHome, component.parent.artifact)
            def buildItemHome = new File(modelFolder, "$item.artifact")
            build(request, buildItemHome)
        }
    }
}