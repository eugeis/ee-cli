package ee.cli.core.integ

import ee.cli.core.Handler
import ee.cli.core.Valid
import ee.cli.core.model.Context
import ee.cli.core.model.Result

import javax.inject.Inject

class ClassBasedHandler extends ItemBasedHandler implements Handler {
    @Inject
    Closure classNameResolver

    protected def doResolve(String itemName) {
        def ret
        //try to resolve as a class
        String className = resolveClassName(itemName)
        try {
            ret = injector.resolve(className)
        } catch (e) {
            //it is not a class
            log.debug 'Can not resolve \'{}\', it seems not a class. {}', className, e
        }
        ret
    }

    protected String resolveClassName(String type) {
        classNameResolver ? classNameResolver(type) : type
    }

}