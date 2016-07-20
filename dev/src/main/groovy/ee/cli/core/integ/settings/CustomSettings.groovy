package ee.cli.core.integ.settings

import ee.cli.core.model.Key

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class CustomSettings extends SettingsBase {
    protected def props

    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    void set(String name, value) {
        pcs.firePropertyChange(name, doSet(name, value), value);
    }

    void setKey(Key key, value) {
        pcs.firePropertyChange(key.name, doSet(key.key, value), value);
    }

    protected def doSet(String name, value) {
        def oldValue = props[name]
        if (value != null) {
            props[name] = value
        } else {
            props.remove(name)
        }
        log.debug('Set {}={}, oldValue={}', name, value, oldValue)
        oldValue
    }

    void clear(String name) {
        set(name, null)
    }

    void clearKey(Key key) {
        setKey(key, null)
    }

    def get(String name) {
        props[name]
    }

    void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}