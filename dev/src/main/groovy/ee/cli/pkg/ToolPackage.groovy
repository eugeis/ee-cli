package ee.cli.pkg

import ee.cli.core.Valid
import ee.cli.core.model.Package
import ee.cli.core.model.PackageState

class ToolPackage extends MetaPackage {

    @Valid
    def install(Map<String, Object> params = null) {

        Package targetPackage = pkgHandler.resolve(targetCoordinate.toCanonicalForm())
        if (targetPackage) {
            String installationName = calculateInstallationFolderName()

            installedFile = new File(workspace.home, installationName)
            if (!installedFile.exists()) {
                installedFile.parentFile.mkdirs()

                log.debug('Move {} to {}.', targetPackage.preparedFile, installedFile)
                ant.move(file: targetPackage.preparedFile.canonicalPath, toFile: installedFile.canonicalPath)

                targetPackage.installedFile = installedFile
                log.info "$name installed to {}", installedFile
            } else {
                log.info('The file {} exists already.', installedFile)
            }
            targetPackage.state = PackageState.INSTALLED
            pkgHandler.applyModelChanges(targetPackage)
            state = PackageState.INSTALLED

        } else {
            log.info "$name can not be installed, because the target package {} can not be resolved", targetCoordinate
        }
    }

    protected String calculateInstallationFolderName() {
        String installationName = targetCoordinate.artifactId
        int pos = installationName.lastIndexOf('-')
        if (pos) {
            installationName = installationName.substring(pos + 1)
        }
        installationName
    }
}
