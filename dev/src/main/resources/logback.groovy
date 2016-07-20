import ch.qos.logback.classic.filter.ThresholdFilter

appender('console', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%-5level %msg%n"
    }
    filter(ThresholdFilter) {
        level = INFO
    }
}

appender("file", FileAppender) {
    file = "ee.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%d{dd.MM.yyyy HH:mm:ss.SSSZ} %-5level [%logger{35}] [%thread] - %msg%exception%n"
    }
}

root(trace, ['console', 'file'])