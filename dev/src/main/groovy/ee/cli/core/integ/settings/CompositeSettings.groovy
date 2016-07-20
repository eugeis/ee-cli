package ee.cli.core.integ.settings

import ee.cli.core.model.Key
import ee.cli.core.model.Settings
import ee.cli.core.model.SingleKey

class CompositeSettings extends SettingsBase {
    Settings parent
    KeySettings current

    void set(String name, value) {
        setKey(buildKey(name), value)
    }

    void setKey(Key key, value) {
        current.setKey(key, value)
    }

    void clear(String name) {
        clearKey(buildKey(name))
    }

    void clearKey(Key key) {
        current.clearKey(key)
    }

    def get(String name) {
        def ret = current.get(buildKey(name).key)
        if (ret == null && parent) {
            ret = parent.get(name)
        }
        ret
    }

    protected Key buildKey(String name) {
        new SingleKey(name: name)
    }
}
