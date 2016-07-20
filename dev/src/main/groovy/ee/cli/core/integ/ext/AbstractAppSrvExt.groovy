package ee.cli.core.integ.ext

import ee.cli.core.model.Result
import ee.cli.service.WildFlyService

import javax.inject.Inject

abstract class AbstractAppSrvExt extends AbstractItemsExt {
    protected static Set<String> deployable = ['ear', 'war'] as Set
    @Inject
    WildFlyService appSrv

    protected Result deploy(List items) {
        Result ret = execute(items, { it.deploy() })
        ret
    }
}