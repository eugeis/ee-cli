package ee.cli.core.integ.settings

import ee.cli.core.model.Key
import ee.cli.core.model.Settings

interface KeySettings extends Settings {
    void setKey(Key key, value)
    void clearKey(Key key)
}
