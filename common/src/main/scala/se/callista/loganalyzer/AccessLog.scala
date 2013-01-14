package se.callista.loganalyzer

import java.text.SimpleDateFormat
import java.util.Date

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
