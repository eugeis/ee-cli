package ee.cli.core.integ

class RunnerException extends RuntimeException {
  int errorCode

  RunnerException(int errorCode) {
    super("Error at process execution, exitCode=$errorCode")
    this.errorCode = errorCode
  }
}