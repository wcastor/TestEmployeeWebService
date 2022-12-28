package employeeservice.domain

import cats.effect.IO

trait EmployeeRepo[T] {

  def create(data: T): IO[Either[String, T]]

  def getAll(): IO[List[Employee]]

}
