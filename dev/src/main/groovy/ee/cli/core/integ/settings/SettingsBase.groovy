package ee.cli.core.integ.settings

import ee.cli.core.model.Base
import ee.cli.core.model.Key

abstract class SettingsBase extends Base implements KeySettings {
    String key

    boolean assertProp(String name) {
        boolean ret = true
        if (this."$name" == null) {
            log.error 'Please provide -P{}=... parameter', name
            ret = false
        }
        ret
    }

    boolean assertProps(String... names) {
        boolean ret = true
        names.each {
            def name ->
                if (this."$name" == null) {
                    log.error("\tPlease provide '-P$name=...' parameter")
                    ret = false
                }
        }
        ret
    }

    def get(String name, defaultValue) {
        def ret = this."$name"
        if (ret == null) {
            ret = defaultValue
        }
        ret
    }

    void setKey(Key key, value) {
        set(key.getKey(), String.valueOf(value))
    }

    void clearKey(Key key) {
        clear(key.getKey())
    }

    def get(Key key) {
        get(key.getKey())
    }

    @Override
    void flags(String... flags) {
        flags?.each { flag ->
            if (flag.startsWith('!')) {
                set(flag.substring(1), false)
            } else {
                set(flag, true)
            }
        }
    }

    @Override
    void params(Map<String, Object> params) {
        params?.each { k, v ->
            set(k, v)
        }
    }

    def propertyMissing(String name, value) {
        set(name, value)
    }

    //for properties with default value
    def propertyMissing(String name) {
        get(name)
    }

    def methodMissing(String name, args) {
        assert args.size() == 1,
                "Please provide default value like 'props.$name(defaultValue)' or use filed access like props.$name'"
        get(name, args[0])
    }
}