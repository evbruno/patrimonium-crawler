
lazy val commonSettings = Seq(
  organization := "etc.bruno",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps", "-language:implicitConversions")
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "patrimonium-root"
  )
  .aggregate(core, crawler, web)

lazy val core = project.in(file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "patrimonium-core"
  )

lazy val crawler = project.in(file("crawler"))
  .settings(commonSettings: _*)
  .dependsOn(core)
  .settings(
    name := "patrimonium-crawler",
    libraryDependencies ++= Seq("com.themillhousegroup" %% "scoup" % "0.4.6"),
    resolvers ++= Seq("Millhouse Bintray" at "http://dl.bintray.com/themillhousegroup/maven")
  )

lazy val web = project.in(file("web"))
  .settings(commonSettings: _*)
  .dependsOn(core)
  .settings(
    name := "patrimonium-web"
  )
