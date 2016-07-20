package ee.cli.core.model

import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory

abstract class Base {
    protected XLogger log = XLoggerFactory.getXLogger(this.class)
}
