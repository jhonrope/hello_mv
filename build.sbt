name := """play-scala-intro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := false

TwirlKeys.templateImports += "models.mv._"
TwirlKeys.templateImports += "models.configuracion._"
TwirlKeys.templateImports += "models.mv.base._"

javaOptions in Test += "-Dwebdriver.chrome.driver=C:\\Users\\Jhony\\AppData\\Local\\Google\\Chrome\\Application\\chromedriver.exe"
javaOptions in Test += "-Dwebdriver.ie.driver=C:\\Program Files\\Internet Explorer\\IEDriverServer.exe"