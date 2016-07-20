package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension
import ee.cli.core.model.Label

class GroovyObjectExt extends AbstractExtension {

    protected void doExtend() {
        def meta = GroovyObject.metaClass

        meta.buildLabel = {
            buildLabel(delegate)
        }

        //for Class and logger
        meta = Class.metaClass

        meta.buildLabel = {
            Class clazz = delegate
            buildLabel(clazz)
        }

    }

    Label buildLabel(Object obj) {
        Label ret
        if (obj) {
            ret = buildLabel(obj.class)
        } else {
            ret = new Label(name: 'null')
        }
        ret
    }

    Label buildLabel(Class<Object> clazz) {
        Label ret
        String[] nameParts = clazz.name.split('\\.')
        if (nameParts.length > 1) {
            ret = new Label(name: nameParts[nameParts.length - 1], category: nameParts[nameParts.length - 2])
        } else {
            ret = new Label(name: nameParts[0])
        }
        ret
    }


}