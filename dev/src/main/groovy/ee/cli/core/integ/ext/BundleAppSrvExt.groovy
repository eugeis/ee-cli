package ee.cli.core.integ.ext

import ee.cli.core.model.Bundle

class BundleAppSrvExt extends AbstractAppSrvExt {

    protected void doExtend() {

        Bundle.metaClass.deploy = {
            Bundle item = delegate
            deploy(item.items())
        }
    }
}