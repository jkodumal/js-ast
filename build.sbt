scalaVersion := "2.10.0-RC5"

organization := "com.catamorphic"

name := "js-ast"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
      "org.scalaz" % "scalaz-core_2.10" % "7.0.0-M7",
      "org.scalaz" % "scalaz-concurrent_2.10" % "7.0.0-M7",
      "play" % "play_2.10" % "2.1-RC1"
)

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)