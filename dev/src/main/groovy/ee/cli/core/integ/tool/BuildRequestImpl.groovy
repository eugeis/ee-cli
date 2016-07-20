package ee.cli.core.integ.tool

import ee.cli.core.model.BuildRequest

class BuildRequestImpl implements BuildRequest {
    List<String> tasks = []
    Map<String, Object> params = [:]
    List<String> flags = []

    @Override
    BuildRequest build() {
        task('build')
        this
    }

    @Override
    BuildRequest clean() {
        task('clean')
        this
    }

    @Override
    BuildRequest test() {
        task('test')
        this
    }

    @Override
    BuildRequest integTest() {
        task('integTest')
        this
    }

    @Override
    BuildRequest acceptanceTest() {
        task('acceptanceTest')
        this
    }

    @Override
    BuildRequest install() {
        task('install')
        this
    }

    @Override
    BuildRequest publish() {
        task('publish')
        this
    }

    @Override
    BuildRequest flag(String flag) {
        flags << flag
        this
    }

    @Override
    BuildRequest task(String task) {
        tasks << task
        this
    }

    @Override
    BuildRequest param(String name, String value) {
        params[name] << value
        this
    }


    @Override
    public String toString() {
        "BuildRequest{${fillToString([]).join(',')}}"
    }

    public List<String> fillToString(List<String> fill) {
        if (tasks) {
            fill << "tasks=$tasks"
        }

        if (params) {
            fill << "params=$params"
        }

        if (flags) {
            fill << "flags=$flags"
        }
        fill
    }
}
