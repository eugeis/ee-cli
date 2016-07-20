package ee.cli.core.integ.api

import ee.cli.core.Nop
import ee.cli.core.integ.Injector
import ee.cli.core.integ.NopInterceptor
import ee.cli.core.model.Item

import javax.inject.Inject
import javax.interceptor.Interceptors

@Interceptors([NopInterceptor])
class Net extends Item {
    @Inject
    Injector injector

    @Nop
    boolean isOpen(def host, def port, String msgPrefix = null) {

        def ant = injector.resolve(Ant)
        ant.condition(property: 'ret') {
            socket(server: "$host", port: "$port")
        }
        ant.ret(msgPrefix ? "${msgPrefix}.isOpen($host,$port)" : null)
    }

    @Nop
    boolean isHttpAvailable(def host, def port, def relativeUrl = '', String msgPrefix = null) {
        def ant = injector.resolve(Ant)
        ant.condition(property: 'ret') {
            http(url: "http://${host}:${port}/$relativeUrl")
        }
        ant.ret(msgPrefix ? "${msgPrefix}.isHttpAvailable($host,$port)" : null)
    }

    @Nop
    boolean waitForPortOpen(def host, def port, String msgPrefix = null, int timeoutInSeconds = 10) {
        def ant = injector.resolve(Ant)
        ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: 'second', timeoutproperty: 'ret') {
            socket(server: "$host", port: "$port")
        }
        !ant.ret(msgPrefix ? "Timeout for ${msgPrefix}.waitForPortOpened($host,$port)" : null)
    }

    @Nop
    boolean waitForPortClose(def host, def port, String msgPrefix = null, int timeoutInSeconds = 10) {
        def ant = injector.resolve(Ant)
        ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: 'second', timeoutproperty: 'ret') {
            not { socket(server: "$host", port: "$port") }
        }
        !ant.ret(msgPrefix ? "Timeout for waitForPortClosed($host,$port)" : null)
    }

    @Nop
    boolean waitForHttpAvailable(def host, def port, def relativeUrl = '', String msgPrefix = null) {
        def ant = injector.resolve(Ant)
        ant.waitfor(maxwait: '3', maxwaitunit: 'minute', checkevery: '500', timeoutproperty: 'ret') {
            http(url: "http://${host}:${port}/$relativeUrl")
        }
        !ant.ret(msgPrefix ? "Timeout for waitForHttpAvailable($host,$port,$relativeUrl)" : null)
    }

    @Nop
    boolean waitForHttpClose(def host, def port, def relativeUrl = '', String msgPrefix = null) {
        def ant = injector.resolve(Ant)
        ant.waitfor(maxwait: '3', maxwaitunit: 'minute', checkevery: '500', timeoutproperty: 'ret') {
            not { http(url: "http://${host}:${port}/$relativeUrl") }
        }
        !ant.ret(msgPrefix ? "Timeout for waitForHttpClosed($host,$port,$relativeUrl)" : null)
    }
}