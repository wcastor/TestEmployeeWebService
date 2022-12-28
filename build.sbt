name := "TestEmployeeWebService"

version := "0.1"

scalaVersion := "2.12.17"

val Http4sVersion      = "1.0.0-M21"
val CirceVersion       = "0.14.0-M5"
val CirceConfigVersion = "0.8.0"
val DoobieVersion      = "1.0.0-RC1"
val H2Version          = "1.4.199"
val CatsVersion        = "2.5.0"
val FlywayVersion      = "5.2.4"
val LogBackVersion     = "1.2.3"
libraryDependencies ++= Seq(
  "org.typelevel"  %% "cats-core"           % CatsVersion,
  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"     %% "http4s-circe"        % Http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
  "org.http4s"     %% "http4s-blaze-client" % Http4sVersion,

  "io.circe"       %% "circe-core"          % CirceVersion,
  "io.circe"       %% "circe-generic"       % CirceVersion,
  "io.circe"       %% "circe-config"        % CirceConfigVersion,

  "org.tpolecat"   %% "doobie-core"         % DoobieVersion,
  "org.tpolecat"   %% "doobie-h2"           % DoobieVersion,
  "org.tpolecat"   %% "doobie-hikari"       % DoobieVersion,

  "com.h2database" % "h2"                   % H2Version,
  "org.flywaydb"   % "flyway-core"          % FlywayVersion,
  "ch.qos.logback" % "logback-classic"      % LogBackVersion
)
