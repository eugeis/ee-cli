package ee.cli.core

import ee.cli.core.integ.ext.*
import ee.cli.core.integ.AbstractExtension

class StaticExtensions extends AbstractExtension {

    protected void doExtend() {
        new XLoggerExt().extend()
        new GroovyObjectExt().extend()
        new StringExt().extend()
        new DateExt().extend()
        new FileExt().extend()
        new InetAddressExt().extend()
        new RuntimeExceptionExt().extend()
    }
}
