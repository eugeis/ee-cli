package ee.cli.bundle

import ee.cli.core.integ.ExpressionResolver
import ee.cli.core.model.Bundle
import ee.cli.dsl.ComponentModel
import ee.mdd.model.component.Model

import javax.annotation.PostConstruct
import javax.inject.Inject

class ComponentBundle extends Bundle {
    List items = []

    @Inject
    List<String> queries

    @Inject
    ComponentModel model

    @Inject
    ExpressionResolver expressionResolver

    @PostConstruct
    void init() {
        Model componentModel = model.load()
        queries.each { expression ->
            def item = expressionResolver.resolve(expression, componentModel)
            if (Collection.isInstance(item)) {
                items.addAll(item)
            } else if (item) {
                items << item
            } else {
                log.warn 'Can\'t load query {} from the model {}', it, model
            }
        }
    }

    @Override
    List<Object> items() {
        items
    }
}
