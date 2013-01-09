package se.callista.loganalyzer

case class Count(
  status: HttpStatus,
  count: Int) {

//  override def toString = "status: %s, count: %s" format (status, count)

  def toJson = "{ \"status\":\"%s\", \"count\": %s }".format(status, count)
  
}

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
