package se.callista.loganalyzer

import java.util.Date
import java.text.SimpleDateFormat

case class LogMessage(host: String, id: Int, log: AccessLog)