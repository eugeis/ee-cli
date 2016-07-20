package ee.cli.core.integ.settings

class SystemEnvSettings extends SettingsBase {
    private static final SystemEnvSettings INSTANCE = new SystemEnvSettings()
    static get() { INSTANCE }
    private SystemEnvSettings() {
        key = 'SystemEnv'
    }

    def sysEnv = System.getenv()

    void set(String name, value) {
        log.warn("The 'setKey' method is not supported in the class $this")
    }

    void clear(String name) {
        log.warn("The 'clearKey' method is not supported in the class $this")
    }

    def get(String name) {
        sysEnv[name]
    }
}