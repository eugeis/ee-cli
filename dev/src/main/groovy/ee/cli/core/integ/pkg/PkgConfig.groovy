package ee.cli.core.integ.pkg

import ee.cli.core.integ.settings.SettingsBase
import ee.cli.core.model.DynamicItem
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate

class PkgConfig extends SettingsBase {
    DynamicItem config
    MavenCoordinate coordinate

    protected Set<String> getByMethods = ['targetCoordinate'] as Set
    protected String _type
    protected String _targetCoordinate
    protected Map<String, Map<String, Object>> _addons

    String getType() {
        if (!_type) {
            if (!config.empty) {
                _type = 'Meta'
                String installerClassName = config.installerClassName
                if (installerClassName) {
                    if (installerClassName.contains('Tool') || installerClassName.contains('FileInjector')) {
                        _type = 'Tool'
                    } else if (installerClassName.contains('MySqlData')) {
                        _type = 'MySqlData'
                    }
                }
            } else {
                _type = 'content'
            }
        }
        _type
    }

    String getTargetCoordinate() {
        if (_targetCoordinate == null) {
            _targetCoordinate = parsePackageId()
        }
        _targetCoordinate
    }

    protected String parsePackageId(DynamicItem base = config) {
        String ret
        DynamicItem packageId = get('packageId', base)
        if (packageId) {
            String group = get('group', '', packageId)
            String name = get('name', '', packageId)
            String version = get('version', '', packageId)
            ret = version ? "$group:$name:$version" : "$group:$name"
        } else {
            ret = ''
        }
        ret
    }

    Map<String, Map<String, Object>> getAddons() {
        if (!_addons) {
            _addons = [:]
            String subDir = get('filesSubDir')
            def dropins = get('dropin')
            if (dropins) {
                if (Collection.isInstance(dropins)) {
                    dropins.each {
                        addAddon(it, subDir)
                    }
                } else {
                    addAddon(dropins, subDir)
                }
            }
        }
        _addons
    }

    protected def addAddon(def dropin, String defaultSubDir = '') {
        String addon = parsePackageId(dropin)
        String target = coordinate.toCanonicalForm()
        def ret = [name            : "${addon}->$target",
                   coordinate      : addon,
                   targetCoordinate: target,
                   dependsOn       : [target]]

        boolean extract = get('unpack', false, dropin)
        if (extract) {
            ret['extract'] = true
        }

        String excludes = get('excludes', null, dropin)
        if (excludes) {
            ret['excludes'] = excludes
        }

        String includes = get('includes', null, dropin)
        if (includes) {
            ret['includes'] = includes
        }

        String subDir = get('subDir', defaultSubDir, dropin)
        if (subDir) {
            ret.subDir = subDir
        }

        _addons[ret.name] = ret
        ret
    }

    def get(String name, DynamicItem base = config) {
        if (getByMethods.contains(name)) {
            this."$name"
        } else if (base.contains(name)) {
            base[name]
        }
    }

    def get(String name, def defaultValue, DynamicItem base = config) {
        def ret = get(name, base)
        if (ret == null) {
            ret = defaultValue
        }
        ret
    }

    @Override
    void set(String name, Object value) {
        config[name] = value
    }

    @Override
    void clear(String name) {
        config[name] = null
    }
}
