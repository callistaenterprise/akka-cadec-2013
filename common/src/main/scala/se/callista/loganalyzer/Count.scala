package se.callista.loganalyzer

case class Count(
  status: HttpStatus, // Success, ClientError or ServerError
  count: Int) {

  def toJson = "{ \"status\":\"%s\", \"count\": %s }".format(status, count)
  
}
