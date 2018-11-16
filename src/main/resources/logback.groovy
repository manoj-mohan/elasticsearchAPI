import ch.qos.logback.classic.AsyncAppender

import static ch.qos.logback.classic.Level.INFO

scan("30 seconds")
def LOG_PATH = "/home/manoj/Projects/POC/elasticsearchAPI/logs"
def LOG_ARCHIVE = "${LOG_PATH}/archive"


appender("Console-Appender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = '%X{requestID}#%date{"HH:mm:ss,SSS"}#%msg#%n'
    }
}
appender("File-Appender", FileAppender) {
    file = "${LOG_PATH}/logfile.log"
    encoder(PatternLayoutEncoder) {
        pattern = '%X{requestID}#%date{"HH:mm:ss,SSS"}#%msg#%n'
        outputPatternAsHeader = true
    }
}

appender("Async-Appender", AsyncAppender) {
    appenderRef("File-Appender")
}
logger("com.ttn.elasticsearchAPI", DEBUG, ["Async-Appender"], false)

root(INFO, ["Console-Appender"])