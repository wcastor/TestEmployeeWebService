package employeeservice.logic.db

import cats.effect.IO
import doobie.util.update.Update0
import doobie.implicits._
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import employeeservice.domain.{ Employee, EmployeeRepo }

private object EmployeeSQL {

  def insert(employee: Employee): Update0 =
    sql"""
         |INSERT INTO employees(
         |  name,
         |  department,
         |  age
         |)
         |VALUES (
         |  ${employee.name},
         |  ${employee.department},
         |  ${employee.age}
         |)
         |""".stripMargin.update

  def getAllWithLimit: Query0[Employee] =
    sql"""
         |SELECT * FROM employees
         |""".stripMargin
      .query[Employee]

}

class EmployeeRepoImpl(xa: Transactor[IO]) extends EmployeeRepo[Employee] {
  override def create(employee: Employee): IO[Either[String, Employee]] =
    EmployeeSQL
      .insert(employee)
      .withUniqueGeneratedKeys[Long]("id")
      .attemptSomeSqlState {
        case state => state.value
      }
      .transact(xa)
      .map {
        case Right(id: Long) => Right(employee.copy(id = Some(id)))
        case Left(err)       => Left(err)
      }

  override def getAll: IO[List[Employee]] =
    EmployeeSQL.getAllWithLimit.to[List].transact(xa)
}
