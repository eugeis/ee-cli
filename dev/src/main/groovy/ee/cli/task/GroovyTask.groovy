package ee.cli.task

import ee.cli.core.model.Context
import ee.cli.core.model.Result
import ee.cli.core.model.Task

import javax.inject.Inject

class GroovyTask extends Task {
    @Inject
    String handler
    @Inject
    String expression

    @Override
    Result execute(Context context, Object source) {
    }
}
