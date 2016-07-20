package ee.cli.core.integ

import org.slf4j.ext.XLogger

class StreamHandler extends Thread {
    protected XLogger log
    private InputStream inputStream
    private String filterPattern
    private boolean filter
    private def outputProcessor

    StreamHandler(InputStream inputStream, boolean filter, String filterPattern, XLogger log, def outputProcessor) {
        this.inputStream = inputStream
        this.filter = filter
        this.filterPattern = filterPattern
        this.log = log
        this.outputProcessor = outputProcessor
    }

    void run() {
        if (this.filter) {
            inputStream.eachLine { line ->
                if (line.toLowerCase() ==~ filterPattern) {
                    handleLine(line)
                }
            }
        } else {
            inputStream.eachLine { handleLine(it) }
        }
    }

    private void handleLine(String line) {
        log.info(line)
        if (outputProcessor) {
            outputProcessor(line)
        }
    }
}