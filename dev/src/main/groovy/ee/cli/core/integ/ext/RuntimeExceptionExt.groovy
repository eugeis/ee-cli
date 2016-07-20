package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension

class RuntimeExceptionExt extends AbstractExtension {

    protected void doExtend() {
        def meta = RuntimeException.metaClass

        meta.hasErrorCode = { int errorCode ->
            RuntimeException exc = delegate
            boolean ret = false
            if (exc.metaClass.hasProperty(exc, "errorCode")) {
                ret = (exc.errorCode == errorCode)
            }
            ret
        }
    }
}