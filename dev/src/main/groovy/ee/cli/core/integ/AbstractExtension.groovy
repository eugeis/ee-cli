package ee.cli.core.integ

import ee.cli.core.model.Base

abstract class AbstractExtension extends Base implements Extension {
    protected static extended = false

    @Override
    void extend() {
        if(!extended) {
            extended = true
        }
        doExtend()
    }

    protected abstract void doExtend();

}
