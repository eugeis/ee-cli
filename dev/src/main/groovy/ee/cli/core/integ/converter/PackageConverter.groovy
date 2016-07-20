package ee.cli.core.integ.converter

import ee.cli.core.model.Package
import ee.cli.handler.PkgHandler

import javax.inject.Inject
import javax.persistence.AttributeConverter

class PackageConverter implements AttributeConverter<Package, String> {

    @Inject
    PkgHandler pkgHandler

    @Override
    String convertToDatabaseColumn(Package pkg) {
        pkg.coordinate
    }

    @Override
    Package convertToEntityAttribute(String coordinate) {
        Package ret
        if(coordinate) {
            ret = pkgHandler.resolve(coordinate)
        }
        ret
    }
}
