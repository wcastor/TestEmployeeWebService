package employeeservice.config

import cats.effect.IO
import io.circe.config.parser
import io.circe.generic.auto._

case class AppConfig(serverConfig: ServerConfig, dbConfig: DBConfig, api: ApiRev)

object AppConfig {
  def load(): IO[AppConfig] =
    for {
      serverConf <- parser.decodePathF[IO, ServerConfig]("server")
      api        <- parser.decodePathF[IO, ApiRev]("api")
      dbConf     <- parser.decodePathF[IO, DBConfig]("database")
    } yield AppConfig(serverConf, dbConf, api)
}
