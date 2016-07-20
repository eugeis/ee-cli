package ee.cli.core.integ.converter

import ee.cli.dsl.ComponentModel
import ee.cli.handler.DslHandler

import javax.inject.Inject
import javax.persistence.AttributeConverter
import java.lang.reflect.Field

class ComponentModelConverter implements AttributeConverter<ComponentModel, String> {

    @Inject
    DslHandler dslHandler

    @Inject
    Object item

    @Inject
    Field target

    @Override
    String convertToDatabaseColumn(ComponentModel model) {
        model.name
    }

    @Override
    ComponentModel convertToEntityAttribute(String componentModelName) {
        dslHandler.resolve(componentModelName)
    }
}
