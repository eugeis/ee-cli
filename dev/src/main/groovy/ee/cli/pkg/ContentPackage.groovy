package ee.cli.pkg

import ee.cli.core.Valid
import ee.cli.core.model.Package

import static ee.cli.core.model.PackageState.PREPARED

class ContentPackage extends Package {
    private static Set<String> extractTypes = ['zip'] as Set

    @Valid
    def prepare(Map<String, Object> params = null) {
        if (extractTypes.contains(resolvedFile.name.extension())) {
            extractFile()
        } else {
            copyFile()
        }
        super.prepare(params)
    }

    protected void copyFile() {
        preparedFile = new File(preparedDir, resolvedFile.name)
        if (!preparedFile.exists()) {
            log.debug('Copy {} to {}.', resolvedFile, preparedFile)
            if (!preparedFile.parentFile.exists()) {
                preparedFile.parentFile.mkdirs()
            }
            ant.copy(file: resolvedFile.canonicalPath, toFile: preparedFile.canonicalPath)
        } else {
            log.info('The file {} exists already.', preparedFile)
        }
    }

    protected void extractFile() {
        preparedFile = new File(preparedDir, coordinate.artifactId)
        if (preparedFile.exists()) {
            log.warn('The folder {} exists already, extract anyway.', preparedFile)
        } else {
            preparedFile.mkdirs()
        }
        log.debug('Unzip {} to {}.', resolvedFile, preparedFile)
        ant.unzip(src: resolvedFile.canonicalPath,
                dest: preparedFile.canonicalPath,
                overwrite: "true")
    }

    @Valid
    def install(Map<String, Object> params = null) {
        log.debug "$name must not be installed."
    }
}
