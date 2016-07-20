package ee.cli.core.integ.settings

class JsonSettings extends CustomSettings {
    static final String KEY_OF_NAME = 'name'
    String name

    def get(String key) {
        if (key == KEY_OF_NAME) {
            name
        } else {
            props[key]
        }
    }
}
