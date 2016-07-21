package ee.cli.handler

import ee.cli.core.integ.JsonModelBasedHandler
import ee.cli.core.integ.NopInterceptor
import ee.cli.core.model.Service

import javax.interceptor.Interceptors
import java.nio.file.Path
import java.nio.file.Paths

@Interceptors([NopInterceptor])
class ServiceHandler extends JsonModelBasedHandler {

    ServiceHandler() {
        classNameResolver = { String className -> "ee.cli.service.${className.capitalize()}Service" }
    }

    protected def executeCallMethod(service, String methodName, def params = null) {
        if ('start'.equalsIgnoreCase(methodName)) {
            startService((Service) service)
        } else if ('stop'.equalsIgnoreCase(methodName)) {
            stopService((Service) service)
        } else {
            super.executeCallMethod(service, methodName, params)
        }
    }

    protected void startService(Service service) {
        if (!service.ping()) {
            List<String> dependsOn = service.dependsOn

            Map<String, Object> fillParamsOfDependencies
            if (dependsOn) {
                fillParamsOfDependencies = [:]
                dependsOn.each { startRequiredService(it, service, fillParamsOfDependencies) }
            }
            log.info 'Start service {}', service
            service.start(fillParamsOfDependencies)

        } else {
            log.info "$service already started."
        }
    }

    protected void stopService(Service service) {
        if (service.ping()) {
            List<String> dependsOnMe = service.dependsOnMe
            if (dependsOnMe) {
                dependsOnMe.each { stopDependentService(it, service) }
            }
            log.info 'Stop service {}', service
            service.stop()
        } else {
            log.info "$service already stoped."
        }
    }

    protected void startRequiredService(String requiredServiceName, Service service,
                                        Map<String, Object> fillParamsOfDependencies) {
        Service requiredService = resolve(requiredServiceName)
        if (requiredService) {
            log.info 'Start required service {} for {}', requiredService, service
            startService(requiredService)
            fillExportedParams(requiredService, fillParamsOfDependencies)
        } else {
            log.warn 'Cannot start the required service {}, because it cannot be resolved.', requiredServiceName
        }
    }

    protected void stopDependentService(String dependentServiceName, Service service) {
        Service dependentService = resolve(dependentServiceName)
        if (dependentService) {
            log.info 'Stop dependent service {} of {}', dependentService, service
            stopService(dependentService)
        } else {
            log.warn 'Cannot stop the dependent service {}, because it cannot be resolved.', dependentServiceName
        }
    }

    def copyLogs(String target) {
        copyLogs(Paths.get(target))
    }

    def copyLogs(Path target) {
        model.model.entrySet().each { Map.Entry entry ->
            Service service = doResolveEntry(entry)
            if (service) {
                service.copyLogs(target)
            } else {
                log.warn('Copy logs of service {} not possible because the service can not be resolved.', entry)
            }
        }
    }

    def copyConfigs(String target) {
        copyConfigs(Paths.get(target))
    }

    def copyConfigs(Path target) {
        model.model.entrySet().each { Map.Entry entry ->
            Service service = doResolveEntry(entry)
            if (service) {
                service.copyLogs(target)
            } else {
                log.warn('Copy configs of service {} not possible because the service can not be resolved.', serviceName)
            }

        }
    }
}