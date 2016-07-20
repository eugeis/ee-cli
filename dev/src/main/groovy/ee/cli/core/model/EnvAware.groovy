package ee.cli.core.model

import java.beans.PropertyChangeListener

interface EnvAware extends PropertyChangeListener {
    Environment getEnv()
}
