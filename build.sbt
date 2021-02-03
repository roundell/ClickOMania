name := "ClickOMania"

version := "1.0"

scalaVersion := "2.13.4"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.12" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test //exclude("junit", "junit-dep")
)

testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v", "-s")