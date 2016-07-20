package ee.cli.core.integ.api

import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory

class Ant extends AntBuilder {
    XLogger log = XLoggerFactory.getXLogger(this.class)

    boolean ret(def checkLabel = null) {
        def ret = (project.properties.ret == 'true')
        if (checkLabel) {
            log.debug "$checkLabel = $ret"
        }
        ret
    }
}