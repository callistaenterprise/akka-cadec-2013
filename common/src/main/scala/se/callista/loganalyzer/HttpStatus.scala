package se.callista.loganalyzer

abstract class HttpStatus 

case object Success extends HttpStatus {
  override def toString = "success"
}

case object ClientError extends HttpStatus{
  override def toString = "clientError"
}

case object ServerError extends HttpStatus{
  override def toString = "serverError"
}
