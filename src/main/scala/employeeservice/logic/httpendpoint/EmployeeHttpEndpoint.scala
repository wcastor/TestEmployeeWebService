package employeeservice.logic.httpendpoint

import cats.effect.IO
import employeeservice.domain.Employee
import employeeservice.logic.db.EmployeeRepoImpl
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder }
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class EmployeeHttpEndpoint(dao: EmployeeRepoImpl) extends Http4sDsl[IO] {

  implicit val decodeEmployee: Decoder[Employee] = deriveDecoder[Employee]
  implicit val encodeEmployee: Encoder[Employee] = deriveEncoder[Employee]

  val service: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "employees" =>
      for {
        result   <- dao.getAll()
        response <- Ok(result.asJson)
      } yield response
  }

}
