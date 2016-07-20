package ee.cli.core.integ.converter

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates

import javax.persistence.AttributeConverter

class MavenCoordinateConverter implements AttributeConverter<MavenCoordinate, String> {

    @Override
    String convertToDatabaseColumn(MavenCoordinate coordinate) {
        coordinate.toCanonicalForm()
    }

    @Override
    MavenCoordinate convertToEntityAttribute(String coordinate) {
        MavenCoordinates.createCoordinate(coordinate)
    }
}
