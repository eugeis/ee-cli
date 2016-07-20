package ee.cli.core.model

interface BuildRequest {
    BuildRequest build()

    BuildRequest clean()

    BuildRequest test()

    BuildRequest integTest()

    BuildRequest acceptanceTest()

    BuildRequest install()

    BuildRequest publish()

    BuildRequest flag(String flag)

    BuildRequest task(String task)

    BuildRequest param(String name, String value)
}
