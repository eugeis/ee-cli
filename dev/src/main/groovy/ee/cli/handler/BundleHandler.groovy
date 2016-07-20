package ee.cli.handler

import ee.cli.core.integ.JsonModelBasedHandler

class BundleHandler extends JsonModelBasedHandler {

    BundleHandler() {
        classNameResolver = { String className -> "ee.cli.bundle.${className.capitalize()}Bundle" }
    }
}