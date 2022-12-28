import cats.effect.IO
import employeeservice.config.{ AppConfig, DBConfig }
import employeeservice.logic.db.EmployeeRepoImpl
import employeeservice.logic.httpendpoint.EmployeeHttpEndpoint
import fs2.StreamApp.ExitCode
import fs2.{ Stream, StreamApp }
import org.http4s.client.blaze.Http1Client
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] with Http4sDsl[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    for {
      config     <- Stream.eval(AppConfig.load())
      transactor <- Stream.eval(DBConfig.transactor(config.dbConfig))
      _          <- Stream.eval(DBConfig.init(transactor))
      _          <- Http1Client.stream[IO]()
      exitCode <- BlazeBuilder[IO]
                   .bindHttp(config.serverConfig.port, config.serverConfig.host)
                   .mountService(new EmployeeHttpEndpoint(new EmployeeRepoImpl(transactor), config.api).service, "/")
                   .serve
    } yield exitCode
}
