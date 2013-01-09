package se.callista.loganalyzer

import java.util.Date
import java.text.SimpleDateFormat

case class AccessLog(
  ip: String,
  timestamp: Date,
  method: String,
  path: String,
  statusCode: Int,
  size: Int) {
  
  
  override def toString = {
    val formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z")
    ip + " - - [" + formatter.format(timestamp) + "] " + method + " " + path + " " + statusCode + " " + size
  }
}

case class LogMessage(host: String, id: Int, log: AccessLog)

case class ConfirmationMessage(id: Int)

case object HandleUnprocessedLogs