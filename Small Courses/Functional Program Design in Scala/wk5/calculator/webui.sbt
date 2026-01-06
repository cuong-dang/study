lazy val webUI = project.in(file("web-ui")).
  enablePlugins(ScalaJSPlugin).
  settings(
    scalaVersion := "2.13.5",
    // Add the sources of the calculator project
    Compile / unmanagedSourceDirectories += baseDirectory.value / ".." / "src" / "main" / "scala" / "calculator",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0",
    scalaJSUseMainModuleInitializer := true
  )
