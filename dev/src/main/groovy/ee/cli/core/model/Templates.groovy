package ee.cli.core.model

import ee.cli.core.integ.ValidInterceptor
import groovy.text.GStringTemplateEngine

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
class Templates extends Item {
    @Inject
    @NotNull
    File home
    String ext = 'txt'
    Map<String, Object> binding

    GStringTemplateEngine engine = new GStringTemplateEngine()

    List<File> loadTemplates() {
        List<File> ret = []
        home.eachFile { ret << it }
        ret
    }

    File loadTemplate(String template) {
        File ret
        ret = templateFile(template)
        if (!ret && template.isInteger()) {
            int index = template.toInteger() - 1
            def items = loadTemplates()
            if (!items.isEmpty() && items.size() >= index) {
                ret = items[index]
            }
        }
        if (!ret) {
            log.warn 'Template {} not found.', template
        }
        ret
    }

    String generate(String name) {
        generate(name, binding)
    }

    String generate(String templateName, Map<String, Object> currentBinding) {
        generate(loadTemplate(templateName), currentBinding)
    }

    String generate(File templateFile, Map<String, Object> currentBinding) {
        def templateEngine = templateEngine(templateFile)
        String ret = templateEngine.make(fillBindingToCurrentBinding(currentBinding))
        ret
    }

    String generateAll(String templateName, List<Map<String, Object>> currentBindings, String separator = '\n') {
        generateAll(templateFile(templateName), currentBindings, separator)
    }

    String generateAll(File templateFile, List<Map<String, Object>> currentBindings, String separator = '\n') {
        def templateEngine = templateEngine(templateFile)
        String ret = currentBindings.collect {
            String text = templateEngine.make(fillBindingToCurrentBinding(it))
            text
        }.join(separator)
        ret
    }

    def templateEngine(File templateFile) {
        String template = templateFile.text
        engine.createTemplate(template)
    }

    File templateFile(String templateName) {
        new File(home, "${templateName}.$ext")
    }

    boolean exists(String templateName) {
        templateFile(templateName).exists()
    }

    private Map<String, Object> fillBindingToCurrentBinding(Map<String, Object> currentBinding) {
        if (currentBinding != binding) {
            binding.each { k, v ->
                if (!currentBinding.containsKey(k)) {
                    currentBinding[k] = v
                }
            }
        }

        //add macro support
        currentBinding.macros = new Templates(home: home, ext: ext, binding: currentBinding)

        currentBinding
    }

    def methodMissing(String templateName, def args) {
        if (args) {
            generate(templateName, args[0] as Map)
        } else {
            generate(templateName, [:])
        }
    }

    def propertyMissing(String templateName) {
        generate(templateName)
    }
}