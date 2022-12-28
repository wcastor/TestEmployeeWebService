package employeeservice.logic.httpendpoint

import cats.effect.IO
import employeeservice.config.ApiRev
import employeeservice.domain.Employee
import employeeservice.logic.db.EmployeeRepoImpl
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder }
import org.http4s.{ HttpService, Response }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class EmployeeHttpEndpoint(dao: EmployeeRepoImpl, api: ApiRev) extends Http4sDsl[IO] {

  implicit val decodeEmployee: Decoder[Employee] = deriveDecoder[Employee]
  implicit val encodeEmployee: Encoder[Employee] = deriveEncoder[Employee]

  val service: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "employees" =>
      for {
        result   <- dao.getAll()
        response <- Ok(result.asJson)
      } yield response
    case GET -> Root / "ping" => Ok({ api.apiPrefix + api.apiVersion }.asJson)
    case req @ POST -> Root / "employee" =>
      for {
        employee <- req.decodeJson[Employee]
        response <- dao.create(employee)
        result   <- check(response)
      } yield result
  }

  def check(res: Either[String, Employee]): IO[Response[IO]] = res match {
    case Left(e)  => InternalServerError(e)
    case Right(r) => Ok(r.asJson)
  }

}
