package ee.cli.dsl

import ee.cli.core.Valid
import ee.cli.core.integ.Relative
import ee.cli.core.integ.converter.MapFileConverter
import ee.cli.core.model.ModelDef
import ee.mdd.generator.java.GeneratorForJava
import ee.mdd.model.component.Component
import ee.mdd.model.component.Model

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.Convert
import javax.validation.constraints.NotNull

class ComponentModel extends ModelDef {
    @Inject
    @NotNull
    GeneratorForJava generator

    @Inject
    @Relative('workspace.home')
    File sourceHome

    @Inject
    String version = 'dev-SNAPSHOT'

    @Inject
    @Relative('workspace.home')
    @Convert(converter = MapFileConverter)
    Map<String, File> componentSourceHomes = [:]

    Model model

    @Valid
    def load() {
        if (!model) {
            model = generator.loadModel(file.toURI().toURL(), facet)
            if (!model.version) {
                model.version = version
            }
            model.findAllDown(Component).each { Component comp ->
                comp.metaClass.sourceHome = componentSourceHomes[comp.name] ?: sourceHome
            }
            log.info "$this loaded: {}", model
        }
        model
    }
}