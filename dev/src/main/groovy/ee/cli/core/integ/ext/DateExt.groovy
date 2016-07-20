package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension

import java.text.SimpleDateFormat

class DateExt extends AbstractExtension {
    protected static SimpleDateFormat longDateTimeFormat = new SimpleDateFormat('dd.MM.yy HH:mm:ss.SSS')
    protected static SimpleDateFormat longTimeFormat = new SimpleDateFormat('HH:mm:ss.SSS')

    protected void doExtend() {
        def meta = Date.metaClass

        meta.longDateTime = {
            longDateTimeFormat.format(delegate)
        }
        meta.longTime = {
            longTimeFormat.format(delegate)
        }
    }
}