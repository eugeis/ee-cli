package ee.cli.core.integ

class ExpressionResolver {

    def resolve(String expression, def base) {
        def ret = base
        expression.split('\\.').each {
            ret = ret."$it"
        }
        ret
    }
}
