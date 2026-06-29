import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*

def sbt2 = "2.0.1"
def sbt1 = "1.12.12"

val commonSettings = Def.settings(
  organization := "com.github.xuwei-k",
  publishTo := (if (isSnapshot.value) None else localStaging.value),
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "2.12" =>
        Seq(
          "-language:higherKinds",
          "-Xsource:3",
        )
      case _ =>
        Nil
    }
  },
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
  ),
  pomExtra := (
    <developers>
    <developer>
      <id>xuwei-k</id>
      <name>Kenji Yoshida</name>
      <url>https://github.com/xuwei-k</url>
    </developer>
  </developers>
  <scm>
    <url>git@github.com:xuwei-k/sbt-root-aggregate.git</url>
    <connection>scm:git:git@github.com:xuwei-k/sbt-root-aggregate.git</connection>
  </scm>
  ),
  description := "check aggregate all sub projects",
  organization := "com.github.xuwei-k",
  homepage := Some(url("https://github.com/xuwei-k/sbt-root-aggregate")),
  licenses := List(
    "MIT License" -> url("https://opensource.org/licenses/mit-license")
  ),
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommandAndRemaining("sonaRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

val `sbt-root-aggregate-root` = rootProject.autoAggregate.settings(
  commonSettings,
  publish / skip := true,
)

val `sbt-root-aggregate` = projectMatrix
  .in(file("sbt-root-aggregate"))
  .enablePlugins(SbtPlugin)
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(
    Seq(sbt1, sbt2).map(scala_version_from_sbt_version.ScalaVersionFromSbtVersion.apply)
  )
  .settings(
    commonSettings,
    sbtTestDirectory := sourceDirectory.value.getParentFile / "test",
    scriptedLaunchOpts ++= Seq[(String, String)](
      "plugin.version" -> version.value,
    ).map { case (k, v) =>
      s"-D${k}=${v}"
    },
    scriptedBufferLog := false,
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" =>
          sbt1
        case "3" =>
          sbt2
      }
    },
  )

ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.6.28"
