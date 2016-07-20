package ee.cli.core.integ.settings

import ee.cli.core.model.Key

class PrefixKey implements Key {
    String prefix
    String name

    String getKey() {
        "${prefix}.$name"
    }
}
