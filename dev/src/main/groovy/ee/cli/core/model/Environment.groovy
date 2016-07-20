package ee.cli.core.model

import ee.cli.core.integ.settings.KeySettings

import java.beans.PropertyChangeListener

interface Environment {
    Settings getBase()
    Settings settings(KeySettings... settings)
    Settings settings(Label label)
    Settings settings(String... keys)

    void addListener(PropertyChangeListener l)
    void removeListener(PropertyChangeListener l)
}
