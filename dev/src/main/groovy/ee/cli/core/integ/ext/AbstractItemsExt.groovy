package ee.cli.core.integ.ext

import ee.cli.core.integ.AbstractExtension
import ee.cli.core.integ.ValidInterceptor
import ee.cli.core.integ.tool.BuildToolImpl
import ee.cli.core.model.BuildRequest
import ee.cli.core.model.Result

import javax.inject.Inject
import javax.interceptor.Interceptors
import javax.validation.constraints.NotNull

@Interceptors([ValidInterceptor])
abstract class AbstractItemsExt extends AbstractExtension {

    protected Result execute(List items, Closure command) {
        Result ret
        if (items) {
            ret = new Result(context: items, results: [])
            items.each {
                def result = command(it)
                if (result) {
                    if (Collection.isInstance(result)) {
                        ret.results.addAll(result)
                    } else {
                        ret.results << result
                    }
                }
            }
            if (ret.results.size() == 1) {
                ret = ret.results.first()
            } else {
                ret.ok = ret.results.find { !it.ok } == null
            }
        }
        ret
    }
}