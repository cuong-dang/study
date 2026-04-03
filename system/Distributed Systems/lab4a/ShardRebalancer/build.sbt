ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "ShardRebalancer",
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.12" % Test
  )
