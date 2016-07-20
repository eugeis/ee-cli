package ee.cli.core.integ.api

import ee.cli.core.model.DynamicItem
import ee.cli.core.model.Item
import org.codehaus.groovy.control.CompilerConfiguration

import javax.annotation.PostConstruct

class Loader extends Item {
    protected GroovyShell shell

    @PostConstruct
    void init() {
        def compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = DelegatingScript.class.name
        shell = new GroovyShell(this.class.classLoader, new Binding(), compilerConfiguration)
    }

    URL loadResourceFromArchive(String resourceFilename, File archive) {
        URL compressedURL = archive.toURI().toURL()
        def entry = "jar:$compressedURL!/$resourceFilename"
        new URL(entry)
    }

    Script parseScript(String scriptFilename, File archive) {
        Script ret
        URL url = loadResourceFromArchive(scriptFilename, archive)
        if (url) {
            try {
                ret = shell.parse(url.toURI())
            } catch (e) {
                log.debug('Cannot parse script {} from {}.', scriptFilename, archive)
            }
        }
        ret
    }

    Script parseScript(String scriptString) {
        Script ret
        try {
            ret = shell.parse(scriptString)
        } catch (e) {
            log.debug('Cannot parse script from {} because of {}', scriptString, e)
        }
        ret
    }

    Closure parseClosure(String scriptString) {
        Closure ret
        try {
            ret = shell.evaluate(scriptString)
        } catch (e) {
            log.debug('Cannot evaluate closure from {} because of {}', scriptString, e)
        }
        ret
    }

    def parseConfig(String scriptFilename, File archive) {
        parseConfig(scriptFilename, archive, new DynamicItem(name: scriptFilename))
    }

    def parseConfig(String scriptFilename, File archive, DynamicItem fillConfig) {
        def ret = fillConfig
        URL url = loadResourceFromArchive(scriptFilename, archive)
        if (url) {
            try {
                DelegatingScript script = shell.parse(url.toURI())
                script.setDelegate(ret)
                script.run()
            } catch (e) {
                log.debug('Cannot parse script {} from {}.', scriptFilename, archive)
            }
        }
        ret
    }

    def applyScript(String scriptFilename, File archive, Binding binding = null) {
        def ret
        try {

            Script script = parseScript(scriptFilename, archive)
            if (script) {
                if (binding) {
                    script.binding = binding
                }
                ret = script.run()
            }
        } catch (e) {
            log.debug('Cannot apply script {} from {}.', scriptFilename, archive)
        }
        ret
    }
}
