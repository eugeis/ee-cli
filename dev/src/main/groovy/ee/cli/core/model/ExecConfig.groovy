package ee.cli.core.model

import org.slf4j.ext.XLogger

import javax.inject.Inject
import java.util.concurrent.TimeUnit

class ExecConfig {
  File home
  def cmd
  def env = [:]
  def filterPattern = '.*(\\.{4}|exception|error|fatal|success).*'
  boolean failOnError = false
  @Inject
  boolean filter = false
  @Inject
  boolean noConsole = false
  boolean wait = true
  long timeout = 10
  TimeUnit timeoutUnit = TimeUnit.SECONDS
  XLogger log
  def outputProcessor

  public String toString(){
    "${home}>${cmd} [wait=$wait, filter=$filter, noConsole=$noConsole, failOnError=$failOnError, ee.cli.env=$env]"
  }
}