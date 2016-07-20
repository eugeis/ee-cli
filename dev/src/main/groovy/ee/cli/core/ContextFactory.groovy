package ee.cli.core

import ee.cli.bundle.ComponentBundle
import ee.cli.core.integ.ext.BundleAppSrvExt
import ee.cli.core.integ.ext.BundleBuildExt
import ee.cli.core.integ.ext.ComponentAppSrvExt
import ee.cli.core.integ.ext.ComponentBuildExt
import ee.cli.core.integ.ext.ComponentInfoExt
import ee.cli.core.integ.CommandFactory
import ee.cli.core.integ.HandlersHandler
import ee.cli.core.integ.InjectorImpl
import ee.cli.core.integ.ext.PathExt
import ee.cli.core.model.Context
import ee.cli.core.model.Environment
import ee.cli.core.model.EnvironmentDelegate
import ee.cli.core.model.Service
import ee.cli.core.model.Tool
import ee.cli.dsl.ComponentModel
import ee.cli.env.CommonEnvironment

class ContextFactory {

    Context parse(String[] args) {
        def env = new CommonEnvironment()

        HandlersHandler handlersHandler = new HandlersHandler()
        InjectorImpl injector = buildInjector(env, handlersHandler)
        handlersHandler = injector.wire(handlersHandler)

        Context ret = new Context(args: args, commands: new ArrayDeque(), env: env,
                handlersHandler: handlersHandler, injector: injector, commandFactory: injector.resolve(CommandFactory))
        ret.addCommands(args.join(' '))

        injector.loadExtentionType(PathExt)

        injector.registerExtension(ComponentModel, ComponentBuildExt)
        injector.registerExtension(ComponentModel, ComponentInfoExt)
        injector.registerExtension(ComponentModel, ComponentAppSrvExt)
        injector.registerExtension(ComponentBundle, BundleBuildExt)
        injector.registerExtension(ComponentBundle, BundleAppSrvExt)

        injector.registerClassResolver(Tool, { Class type -> handlersHandler.resolve('tool').resolve(type) })
        injector.registerClassResolver(Service, { Class type -> handlersHandler.resolve('service').resolve(type) })
        injector.registerClassResolver(Environment, { Class type -> handlersHandler.resolve('env').resolve(type) })

        ret
    }

    protected InjectorImpl buildInjector(Environment env, HandlersHandler handlersHandler) {
        InjectorImpl ret = new InjectorImpl(env:
                new EnvironmentDelegate(target: env), handlersHandler: handlersHandler)
        ret.init()
        ret
    }
}
