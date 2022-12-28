package employeeservice.config
import cats.effect.IO
import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

case class DBConfig(driver: String, url: String, user: String, password: String, poolSize: Int)

object DBConfig {

  def transactor(dbConfig: DBConfig, ec: ExecutionContext): IO[HikariTransactor[IO]] = {

    val config = new HikariConfig()
    config.setJdbcUrl(dbConfig.url)
    config.setUsername(dbConfig.user)
    config.setPassword(dbConfig.password)
    config.setMaximumPoolSize(dbConfig.poolSize)

    val transactor: IO[HikariTransactor[IO]] =
      IO.pure(HikariTransactor.apply[IO](new HikariDataSource(config), ec))
    transactor
  }

  def init(transactor: HikariTransactor[IO]): IO[Unit] =
    transactor.configure { dbSource =>
      IO {
        Flyway.configure().dataSource(dbSource).load().migrate()
      }
    }
}
