package ee.cli.core.model

class Result extends Item {
    def context
    Boolean ok = true
    def outcome
    Throwable error
    String failure
    String info
    List<Result> results

    String toString() {
        fillToString(new StringBuffer()).toString()
    }

    protected StringBuffer fillToString(StringBuffer fill, boolean onlyFailed = false) {
        //don't print groups, only really results
        if (!results && (!onlyFailed || !ok)) {
            fill.append(ok ? 'SUCCESS: ' : 'FAILED: ')
            String sep = ''
            if (outcome != null) {
                fill.append(sep).append(outcome)
                sep = ','
            }
            if (info != null) {
                fill.append(sep).append(info)
                sep = ','
            }
            if (failure != null) {
                fill.append(sep).append(failure)
                sep = ','
            }
            if (error != null) {
                fill.append(sep).append(error)
                sep = ','
            }
            if (context != null) {
                fill.append(sep).append(context)
            }
            fill.append('\n')
        }
        fill
    }

    String toStringDetails(boolean onlyFailed = false) {
        if (!onlyFailed || !ok) {
            fillToStringDetails(new StringBuffer(), onlyFailed).toString()
        }
    }

    protected StringBuffer fillToStringDetails(StringBuffer fill, boolean onlyFailed = false) {
        fillToString(fill, onlyFailed)
        results?.each { it.fillToStringDetails(fill, onlyFailed) }
        fill
    }

    protected StringBuffer fillToJson(StringBuffer fill) {
        fill.append('{').append('"ok":').append(ok)
        String sep = ','
        if (info != null) {
            fill.append(sep).append('"info":"').append(info).append('"')
        }
        if (failure != null) {
            fill.append(sep).append('"failure":"').append(failure).append('"')
        }
        if (error != null) {
            fill.append(sep).append('"error":"').append(error).append('"')
        }
        if (context != null) {
            fill.append(sep).append('"context":"').append(context).append('"')
        }
        if (outcome != null) {
            fill.append(sep).append('"outcome":"').append(outcome).append('"')
        }
        if (results) {
            fill.append(sep).append('"results":[')
            sep = ''
            results.each {
                fill.append(sep)
                it.fillToJson(fill)
                sep = ','
            }
            fill.append(']')
        }
        fill.append('}')
        fill
    }

    String toJson() {
        fillToJson(new StringBuffer()).toString()
    }
}
