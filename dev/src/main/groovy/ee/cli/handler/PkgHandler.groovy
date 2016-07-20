package ee.cli.handler

import ee.cli.core.integ.api.Loader
import ee.cli.core.integ.DependencyResolver
import ee.cli.core.integ.JsonModelBasedHandler
import ee.cli.core.integ.pkg.PkgConfig
import ee.cli.core.model.*
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates

import javax.inject.Inject
import javax.validation.constraints.NotNull

import static ee.cli.core.model.PackageState.*

class PkgHandler extends JsonModelBasedHandler {
    @Inject
    @NotNull
    Loader loader

    @Inject
    @NotNull
    DependencyResolver dependencyResolver

    def prepareRequested = [] as Set
    def installRequested = [] as Set

    PkgHandler() {
        classNameResolver = { String className -> "ee.cli.pkg.${className.capitalize()}Package" }
    }

    protected Result doExecute(Context context, source) {
        Result ret
        Command command = context.commands.poll()

        Object item = resolve(command)

        if (item) {
            if (context.commands) {
                String callable = context.commands.poll()
                ret = execute(item, callable, context, source)
            } else {
                ret = execute(item, '', context, source)
            }
        } else {
            ret = new Result(context: context, ok: false, failure: "Cannot resolve item for $command agains the $context")
        }

        ret
    }

    protected def executeCallMethod(pkg, String methodName) {
        if ('prepare'.equalsIgnoreCase(methodName)) {
            preparePackage((Package) pkg)
        } else if ('install'.equalsIgnoreCase(methodName)) {
            installPackage((Package) pkg)
        } else if ('update'.equalsIgnoreCase(methodName)) {
            updatePackage((Package) pkg)
        } else if ('updateSingle'.equalsIgnoreCase(methodName)) {
            updatePackage((Package) pkg, false, false)
        } else if ('updateAll'.equalsIgnoreCase(methodName)) {
            updatePackage((Package) pkg, true, true)
        } else if ('uninstall'.equalsIgnoreCase(methodName)) {
            uninstallPackage((Package) pkg)
        } else if ('uninstallSingle'.equalsIgnoreCase(methodName)) {
            uninstallPackage((Package) pkg, false, false)
        } else if ('uninstallAll'.equalsIgnoreCase(methodName)) {
            uninstallPackage((Package) pkg, true, true)
        } else {
            super.executeCallMethod(pkg, methodName)
        }
    }

    protected Package resolvePackage(Package pkg) {
        def ret = pkg
        if (RESOLVED.order > pkg.state.order || !pkg.resolvedFile?.exists()) {
            if (pkg.coordinate.version) {
                def entry = resolveHierarchy(pkg.coordinate)
                if (entry) {
                    ret = doResolveEntry entry
                }
            } else {
                //resolve addons and similar link/meta dependencies
                def entry = findEntry(pkg.coordinate)
                if (entry) {
                    Package resolved = doResolveEntry entry
                    pkg.resolvedFile = resolved.resolvedFile
                    pkg.state = RESOLVED
                    applyModelChanges(pkg)
                }
            }
        } else {
            log.info 'The package {} is already resolved.', pkg
        }
        ret
    }

    protected Package preparePackage(Package pkg) {
        if (!prepareRequested.contains(pkg.name)) {
            prepareRequested << pkg.name
            if (PREPARED.order > pkg.state.order) {

                if (!pkg.state.resolved) {
                    pkg = resolvePackage(pkg)
                }

                if (pkg) {
                    List<String> dependsOn = pkg.dependsOn
                    Map<String, Object> fillParamsOfDependencies
                    if (dependsOn) {
                        fillParamsOfDependencies = [:]
                        dependsOn.each { preparePackage(it, pkg, fillParamsOfDependencies) }
                    }
                    log.info 'Prepare package {}', pkg
                    pkg.prepare(fillParamsOfDependencies)
                    applyModelChanges(pkg)
                }
            } else {
                log.info 'The package {} is already prepared.', pkg
            }

            def metaEntries = findEntriesByTargetAndStateLessThen(pkg.coordinate, PREPARED)
            if (metaEntries) {
                log.info 'Prepare addons for {}.', pkg
                metaEntries.each { preparePackage(doResolveEntry(it)) }
            }

            prepareRequested.remove(pkg.name)
        } else {
            log.debug 'The preparing of the package {} is already requested.', pkg
        }
        pkg
    }

    protected Package installPackage(Package pkg) {
        if (!installRequested.contains(pkg.name)) {
            installRequested << pkg.name
            if (INSTALLED.order > pkg.state.order) {

                if (!pkg.state.prepared) {
                    pkg = preparePackage(pkg)
                }

                List<String> dependsOn = pkg.dependsOn

                Map<String, Object> fillParamsOfDependencies
                if (dependsOn) {
                    fillParamsOfDependencies = [:]
                    dependsOn.each { installPackage(it, pkg, fillParamsOfDependencies) }
                }
                log.info 'Install package {}', pkg
                pkg.install(fillParamsOfDependencies)
                applyModelChanges(pkg)
            } else {
                log.info 'The package {} is already installed.', pkg
            }

            def metaEntries = findEntriesByTargetAndStateLessThen(pkg.coordinate, INSTALLED)
            if (metaEntries) {
                log.info 'Install addons for {}.', pkg
                metaEntries.each { installPackage(doResolveEntry(it)) }
            }

            installRequested.remove(pkg.name)
        } else {
            log.debug 'The installation of the package {} is already requested.', pkg
        }
        pkg
    }

    protected Package updatePackage(Package pkg,
                                    boolean updateDependsOnMe = false, boolean updateDependsOn = false) {
        uninstallPackage(pkg, updateDependsOnMe, updatePackage())
        installPackage(pkg)
        pkg
    }

    protected Package uninstallPackage(Package pkg,
                                       boolean uninstallDependsOnMe = true, boolean uninstallDependsOn = false) {
        if (UNINSTALLED.order < pkg.state.order) {
            List<String> dependsOnMe = pkg.dependsOnMe
            List<String> dependsOn = pkg.dependsOn

            if (uninstallDependsOnMe) {
                if (dependsOnMe) {
                    dependsOnMe.each { uninstallPackage(it, pkg, uninstallDependsOnMe, uninstallDependsOn) }
                }
            } else if (dependsOnMe) {
                log.warn 'Skip the uninstalling of the following packages {} which are depends on {}.', dependsOnMe, pkg
            }

            log.info 'Uninstall the package {}', pkg
            pkg.uninstall()

            if (uninstallDependsOn && dependsOn) {
                log.info 'As requested, also the following packages {} will be uninstalled which are needed for {}.', dependsOn, pkg
                dependsOn.each { uninstallPackage(it, pkg, uninstallDependsOnMe, uninstallDependsOn) }
            }
            applyModelChanges(pkg)
        } else {
            log.info 'The package {} is already uninstalled.', pkg
        }
        pkg
    }

    protected Package preparePackage(String coordinate, Package trigger,
                                     Map<String, Object> fillParamsOfDependencies) {
        Package pkg = resolve(coordinate)
        if (pkg) {
            log.info 'Prepare the package {} triggered by{}', pkg, trigger
            pkg = preparePackage(pkg)
            fillExportedParams(pkg, fillParamsOfDependencies)
        } else {
            log.warn 'Cannot prepare the package {} triggered by {}, because it cannot be resolved.', coordinate, trigger
        }
        pkg
    }

    protected Package installPackage(String coordinate, Package trigger,
                                     Map<String, Object> fillParamsOfDependencies) {
        Package pkg = resolve(coordinate)
        if (pkg) {
            log.info 'Install the package {} triggered by {}', pkg, trigger
            installPackage(pkg)
            fillExportedParams(pkg, fillParamsOfDependencies)
        } else {
            log.warn 'Cannot install the package {} triggered by {}, because it cannot be resolved.', coordinate, trigger
        }
        pkg
    }

    protected Package uninstallPackage(String entryName, Package trigger,
                                       boolean uninstallDependsOnMe = true, boolean uninstallDependsOn = false) {
        Package pkg = resolve(entryName)
        if (pkg) {
            log.info 'Uninstall the package {} triggered by {}', pkg, trigger
            uninstallPackage(pkg, uninstallDependsOnMe, uninstallDependsOn)
        } else {
            log.warn 'Cannot uninstall the package {} triggered by {}, because it cannot be resolved.', entryName, trigger
        }
        pkg
    }

    protected def doResolve(String nameOrCoordinate) {
        def entry = findEntryByName(nameOrCoordinate)
        if (!entry) {
            MavenCoordinate coordinate = MavenCoordinates.createCoordinate(nameOrCoordinate)
            entry = findEntry(coordinate)
            if (!entry && coordinate.version) {
                entry = resolveHierarchy(coordinate)
            }
        }

        if (entry) {
            doResolveEntry entry
        }
    }

    protected def resolveHierarchy(MavenCoordinate coordinate) {
        def ret
        MavenResolvedArtifact[] resolved = dependencyResolver.resolve(coordinate)
        if (resolved) {
            resolved.each { MavenResolvedArtifact resolvedArtifact ->
                File file = resolvedArtifact.asFile()
                def pkgEntry = findEntry(resolvedArtifact.coordinate)
                if (!pkgEntry) {
                    PkgConfig config = parseConfig(file, resolvedArtifact.coordinate)
                    Package pkg = addNewPackage(config, resolvedArtifact)
                    addAddons(pkg, config)
                } else {
                    updateResolvedEntry(pkgEntry, resolvedArtifact)
                }
            }

            //link reverse
            fillDependsOnMes(resolved)
            writeModel()
            ret = findEntry(coordinate)
        }
        ret
    }

    protected def updateResolvedEntry(def pkgEntry, MavenResolvedArtifact resolvedArtifact) {
        def pkg = pkgEntry.value
        pkg.resolvedFile = resolvedArtifact.asFile().canonicalPath
        PackageState state = PackageState.findByName(pkg.state)
        if (state.order < RESOLVED.order) {
            pkg.state = RESOLVED.name()
        }
    }

    protected void fillDependsOnMes(MavenResolvedArtifact[] resolved) {
        resolved.each { MavenResolvedArtifact resolvedArtifact ->
            resolvedArtifact.dependencies.each {
                def entry = findEntry(it.coordinate)
                entry = entry ?: resolveHierarchy(it.coordinate)
                if (entry) {
                    def pkg = entry.value
                    String canonicalForm = resolvedArtifact.coordinate.toCanonicalForm()
                    if (!pkg.dependsOnMe.contains(canonicalForm)) {
                        pkg.dependsOnMe << canonicalForm
                    }
                } else {
                    log.warn 'Cannot resolve required dependency {}', it.coordinate
                }
            }
        }
    }

    protected Package addNewPackage(PkgConfig config, MavenResolvedArtifact resolvedArtifact) {
        def props =
                [name        : resolvedArtifact.coordinate.toCanonicalForm(),
                 dependsOn   : resolvedArtifact.dependencies.collect {
                     it.coordinate.toCanonicalForm()
                 },
                 dependsOnMe : [],
                 coordinate  : resolvedArtifact.coordinate.toCanonicalForm(), state: RESOLVED.name(),
                 resolvedFile: resolvedArtifact.asFile().canonicalPath]

        Package ret = add(props, config)
        ret
    }

    protected void addAddons(Package targetPackage, PkgConfig config) {
        def addons = config.addons
        if (addons) {
            String type = 'addon'
            String className = resolveClassName(type)

            addons.each { name, pkgEntry ->
                if (!model.data(pkgEntry.name)) {
                    Package pkg = injector.resolve(className, true, pkgEntry)
                    targetPackage.dependsOnMe << pkg.name
                    applyModelChanges(pkg, true)
                    model.data(pkg.name).type = type
                    resolve(pkg.coordinate.toCanonicalForm())
                } else {
                    log.info 'Skip the the addon {}, because it exists already.', pkgEntry.name
                }
            }
            applyModelChanges(targetPackage)
        }
    }

    protected PkgConfig parseConfig(File file, MavenCoordinate coordinate) {
        DynamicItem config = loader.parseConfig('prepareConfig.gradle', file, new DynamicItem())
        loader.parseConfig('installer.gradle', file, config)
        new PkgConfig(config: config, coordinate: coordinate)
    }

    protected def findEntryByName(String entryName) {
        def ret = model.find { String itemName, def itemDef ->
            itemName == entryName
        }
        ret
    }

    protected def findEntry(MavenCoordinate coordinate, boolean withVersion = true) {
        def ret = model.find { String itemName, def itemDef ->
            MavenCoordinate currentCoordinate = MavenCoordinates.createCoordinate(itemDef.coordinate)
            boolean ok = coordinate.groupId == currentCoordinate.groupId && coordinate.artifactId == currentCoordinate.artifactId
            if (ok) {
                if (withVersion) {
                    ok = coordinate.version ? coordinate.version == currentCoordinate.version : currentCoordinate.version
                } else {
                    ok = !coordinate.version || (coordinate.version == currentCoordinate.version)
                }
            }
            ok
        }
        ret
    }

    protected def findEntriesByTargetAndStateLessThen(MavenCoordinate coordinate, PackageState lessThen) {
        def ret = model.findAll { String itemName, def itemDef ->
            boolean ok = false
            if (itemDef.targetCoordinate) {
                MavenCoordinate currentCoordinate = MavenCoordinates.createCoordinate(itemDef.targetCoordinate)
                ok = coordinate.groupId == currentCoordinate.groupId && coordinate.artifactId == currentCoordinate.artifactId &&
                        (!coordinate.version || coordinate.version == currentCoordinate.version)
                ok = ok && PackageState.findByName(itemDef.state).order < lessThen.order
            }
            ok
        }
        ret
    }

}
