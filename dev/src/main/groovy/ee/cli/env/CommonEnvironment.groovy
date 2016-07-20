package ee.cli.env

import ee.cli.core.model.EnvironmentImpl

import javax.inject.Inject

class CommonEnvironment extends EnvironmentImpl {
    @Inject
    String name
}
