package ee.cli.service

import ee.cli.core.Valid
import ee.cli.core.integ.api.Net
import ee.cli.core.integ.Export
import ee.cli.core.model.Service

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.validation.constraints.NotNull

class SocketService extends Service {
    @Inject
    Net net
    @Inject
    @NotNull
    @Export
    String host
    @Inject
    @NotNull
    @Export
    Integer port

    @PostConstruct
    void init() {
        if (!host) {
            host = defaultHost
        }

        if (!port) {
            port = defaultPort
        }
    }

    @Valid
    def ping() {
        net.isOpen(host, port, name)
    }

    protected String getDefaultHost() { 'localhost' }

    protected int getDefaultPort() { 80 }

    String toString() {
        "${super.toString()}($host:$port)"
    }
}
