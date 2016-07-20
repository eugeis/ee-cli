package ee.cli.core.integ.converter

import ee.cli.core.integ.ExpressionResolver

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.AttributeConverter
import java.lang.reflect.Field

class MapConverter implements AttributeConverter<Map, String> {

    @Inject
    Object item

    @Inject
    Field target

    @Override
    String convertToDatabaseColumn(Map map) {
        map.collect { k, v -> "$k:$v" }.join(',')
    }

    @Override
    Map convertToEntityAttribute(String mapAsString) {
        mapAsString.split(',').collectEntries {
            int pos = it.indexOf(':')
            [(it.substring(0, pos)): it.substring(pos + 1)]
        }
    }
}
