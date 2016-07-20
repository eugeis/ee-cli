package ee.cli.core.integ.settings

import ee.cli.core.model.Key

class PrefixCompositeSettings extends CompositeSettings {
    String prefix

    @Override
    protected Key buildKey(String name) {
        new PrefixKey(prefix: prefix, name: name)
    }
}
