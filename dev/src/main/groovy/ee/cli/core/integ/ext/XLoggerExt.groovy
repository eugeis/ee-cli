package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension
import org.slf4j.ext.XLogger

class XLoggerExt extends AbstractExtension {

    protected void doExtend() {
        def meta = XLogger.metaClass

        meta.error = { Object... objs -> delegate.error('{}', *objs) }
        meta.warn = { Object... objs -> delegate.warn('{}', *objs) }
        meta.info = { Object... objs -> delegate.info('{}', *objs) }
        meta.debug = { Object... objs -> delegate.debug('{}', *objs) }
    }
}