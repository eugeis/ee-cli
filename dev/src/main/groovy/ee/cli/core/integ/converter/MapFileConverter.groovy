package ee.cli.core.integ.converter

import ee.cli.core.integ.ExpressionResolver

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.AttributeConverter
import java.lang.reflect.Field

class MapFileConverter implements AttributeConverter<Map, String> {

    @Inject
    ExpressionResolver expressionResolver

    @Inject
    Object item

    @Inject
    Field target

    RelativeFileConverter relativeFileConverter

    @PostConstruct
    void init() {
        relativeFileConverter = new RelativeFileConverter(expressionResolver: expressionResolver, item: item, target: target)
    }

    @Override
    String convertToDatabaseColumn(Map map) {
        map.collect { String k, File v -> "$k:${relativeFileConverter.convertToDatabaseColumn(v)}" }.join(',')
    }

    @Override
    Map convertToEntityAttribute(String mapAsString) {
        mapAsString.split(',').collectEntries {
            int pos = it.indexOf(':')
            [(it.substring(0, pos)): relativeFileConverter.convertToEntityAttribute(it.substring(pos + 1))]
        }
    }
}
