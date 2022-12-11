ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "WordCount"
  )
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.2" % Test classifier "tests",
  "org.apache.spark" %% "spark-streaming" % "3.2.1",
  "org.apache.spark" %% "spark-streaming" % "3.2.1" % Test,
  "org.apache.spark" %% "spark-streaming" % "3.2.1" % Test classifier "tests",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test"
)
