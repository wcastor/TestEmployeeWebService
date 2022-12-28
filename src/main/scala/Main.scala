import cats.data.Kleisli
import cats.effect.{ ExitCode, IO, IOApp }
import doobie.util.transactor.Transactor
import employeeservice.config.{ AppConfig, DBConfig }
import employeeservice.logic.db.EmployeeRepoImpl
import employeeservice.logic.httpendpoint.EmployeeHttpEndpoint
import fs2.Stream
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{ Request, Response }

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp with Http4sDsl[IO] {

  def makeRouter(transactor: Transactor[IO], config: AppConfig): Kleisli[IO, Request[IO], Response[IO]] = {
    val routes = new EmployeeHttpEndpoint(new EmployeeRepoImpl(transactor), config.api).routes
    Router[IO](
      "/" -> routes
    ).orNotFound
  }

  def serveStream(transactor: Transactor[IO], appConfig: AppConfig): Stream[IO, ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(appConfig.serverConfig.port, appConfig.serverConfig.host)
      .withHttpApp(makeRouter(transactor, appConfig))
      .serve

  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      config     <- Stream.eval(AppConfig.load())
      transactor <- Stream.eval(DBConfig.transactor(config.dbConfig, global))
      _          <- Stream.eval(DBConfig.init(transactor))
      exitCode   <- serveStream(transactor, config)
    } yield exitCode
    stream.compile.drain.as(ExitCode.Success)
  }
}
