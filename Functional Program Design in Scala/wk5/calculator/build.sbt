course := "progfun2"
assignment := "calculator"

scalaVersion := "2.13.5"
scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")
libraryDependencies += "org.scalameta" %% "munit" % "0.7.22" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0"

testFrameworks += new TestFramework("munit.Framework")
