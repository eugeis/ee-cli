package ee.cli.core.integ.converter

import javax.inject.Inject
import javax.persistence.AttributeConverter
import java.lang.reflect.Field

class EnumConverter implements AttributeConverter<Enum, String> {

    @Inject
    Field target

    @Override
    String convertToDatabaseColumn(Enum literal) {
        literal.name()
    }

    @Override
    Enum convertToEntityAttribute(String literal) {
        Enum.valueOf(target.type, literal)
    }
}
