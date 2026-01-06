course := "parprog1"
assignment := "scalashop"

scalaVersion := "2.13.5"
scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")
libraryDependencies ++= Seq(
  "com.storm-enroute" %% "scalameter-core" % "0.21",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0",
  "org.scalameta" %% "munit" % "0.7.22" % Test
)

testFrameworks += new TestFramework("munit.Framework")
