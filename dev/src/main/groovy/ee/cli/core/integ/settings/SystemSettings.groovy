package ee.cli.core.integ.settings

class SystemSettings extends SettingsBase {
    private static final SystemSettings INSTANCE = new SystemSettings()

    static get() { INSTANCE }

    private SystemSettings() {
        key = 'SystemProperties'
    }

    void set(String name, value) {
        String newValue = String.valueOf(value)
        String oldValue = System.setProperty(name, newValue)
        log.debug('Set {}={}, oldValue={}', name, newValue, oldValue)
    }

    void clear(String name) {
        String oldValue = System.clearProperty(name)
        log.debug('Clear {}, oldValue={}', name, oldValue)
    }

    def get(String name) {
        System.getProperty(name)
    }
}