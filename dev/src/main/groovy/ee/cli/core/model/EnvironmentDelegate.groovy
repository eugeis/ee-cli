package ee.cli.core.model

import ee.cli.core.integ.settings.KeySettings

import java.beans.PropertyChangeListener

class EnvironmentDelegate implements Environment {
    Environment target

    @Override
    Settings getBase() {
        target.base
    }

    @Override
    Settings settings(KeySettings... settings) {
        target.settings(settings)
    }

    @Override
    Settings settings(Label label) {
        target.settings(label)
    }

    @Override
    Settings settings(String... keys) {
        target.settings(keys)
    }

    @Override
    void addListener(PropertyChangeListener l) {
        target.addListener(l)
    }

    @Override
    void removeListener(PropertyChangeListener l) {
        target.removeListener(l)
    }
}
