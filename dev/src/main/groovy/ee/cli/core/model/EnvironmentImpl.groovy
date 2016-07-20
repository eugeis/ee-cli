package ee.cli.core.model

import ee.cli.core.integ.settings.*
import ee.cli.core.model.Environment
import ee.cli.core.model.Item
import ee.cli.core.model.Label
import ee.cli.core.model.Settings

import java.beans.PropertyChangeListener

class EnvironmentImpl extends Item implements Environment {
    protected CustomSettings custom = new CustomSettings(props: [:])

    protected Settings base = new CompositeSettings(parent: SystemEnvSettings.get(),
            current: new CompositeSettings(parent: SystemSettings.get(), current: custom))

    Map<String, CompositeSettings> keyToSettings = [:]

    Settings settings(KeySettings... settingsList) {
        String combinedKey = settingsList.findAll { it.key }.collect { it.key }.join('.')
        CompositeSettings ret
        if (combinedKey) {
            ret = keyToSettings[combinedKey]
        }
        if (!ret) {
            Settings parent = base
            for (KeySettings current : settingsList) {
                ret = new CompositeSettings(parent: parent, current: current)
                parent = ret
            }
            if (combinedKey) {
                keyToSettings[combinedKey] = ret
            }
        }
        ret
    }

    Settings settings(Label label) {
        settings(label.category, label.name)
    }

    Settings settings(String... keys) {
        Settings ret
        if (keys) {
            List<String> fullPrefixes = buildFullPrefixes(keys)
            String lastKey = fullPrefixes[fullPrefixes.size() - 1]
            ret = keyToSettings[lastKey]

            if (!ret) {
                //build settings
                Settings parentSettings = base
                for (String fullPrefix : fullPrefixes) {
                    ret = keyToSettings[fullPrefix]
                    if (!ret) {
                        ret = new PrefixCompositeSettings(key: lastKey, prefix: fullPrefix, current: custom, parent: parentSettings)
                        keyToSettings[fullPrefix] = ret
                    }
                    parentSettings = ret
                }
            }
        } else {
            ret = base
        }
        ret
    }

    Settings getBase() {
        base
    }

    protected List<String> buildFullPrefixes(String... childPrefixes) {
        List<String> ret = []
        StringBuffer parent = new StringBuffer()
        for (String prefix : childPrefixes) {
            parent.append(prefix)
            ret << parent.toString()
            parent.append('.')
        }
        ret
    }

    void addListener(PropertyChangeListener listener) {
        custom.addListener(listener)
    }

    void removeListener(PropertyChangeListener listener) {
        custom.removeListener(listener)
    }
}
