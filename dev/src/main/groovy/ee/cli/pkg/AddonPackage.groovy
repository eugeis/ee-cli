package ee.cli.pkg

import ee.cli.core.Valid
import ee.cli.core.model.Package
import ee.cli.core.model.PackageState

import javax.inject.Inject

class AddonPackage extends MetaPackage {
    @Inject
    boolean extract = false
    @Inject
    String includes = '**/*'
    @Inject
    String excludes
    @Inject
    String subDir

    @Valid
    def install(Map<String, Object> params = null) {
        Package targetPackage = pkgHandler.resolve(targetCoordinate.toCanonicalForm())
        if (targetPackage && targetPackage.state.installed) {
            ant.copy(todir: targetPackage.installedFile, overwrite: true) {
                fileSet()
            }
            state = PackageState.INSTALLED
        } else {
            log.info "$name can not be installed, because the target package {} is not installed.", targetCoordinate
        }
    }

    protected def fileSet() {
        String subDirPrefix = subDir ? "$subDir/" : ''
        ant.mappedresources {
            if (extract) {
                if (preparedFile.isDirectory()) {
                    fileset(dir: preparedFile.canonicalPath) {
                        include(name: includes)
                        exclude(name: excludes)
                    }
                } else {
                    zipfileset(src: preparedFile.canonicalPath) {
                        include(name: includes)
                        exclude(name: excludes)
                    }
                }
                globmapper(from: '*', to: "${subDirPrefix}*")
            } else {
                fileset(file: preparedFile.canonicalPath)
                globmapper(from: '*', to: "${subDirPrefix}${coordinate.artifactId}.${coordinate.type}")
            }
        }
    }
}
