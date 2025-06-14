package sbt_root_aggregate

import sbt.Keys._
import sbt._

object SbtRootAggregate extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    val sbtRootAggregateCheck = taskKey[Unit]("")
    val sbtRootAggregateCheckOnLoad = settingKey[Boolean]("")
    val sbtRootAggregateExclude = settingKey[Seq[String]]("")
  }

  import autoImport._

  override val buildSettings: Seq[Def.Setting[?]] = Def.settings(
    sbtRootAggregateExclude := Nil,
    sbtRootAggregateCheckOnLoad := true,
    sbtRootAggregateCheck := {
      val exclude = sbtRootAggregateExclude.value.toSet
      val aggregateProjects = (LocalRootProject / thisProject).value.aggregate.map(_.project).toSet
      val unit = Project.extract(state.value).currentUnit
      val allProjects = unit.projects.map(_.id).toSeq.sorted
      val diff = allProjects.filterNot(exclude).filterNot(aggregateProjects).filterNot(_ == unit.root)
      if (diff.nonEmpty) {
        sys.error(
          s"please aggregate all projects ${diff.mkString(" ")}"
        )
      }
    },
    Global / onLoad := { state1 =>
      val state2 = (Global / onLoad).value(state1)
      if (sbtRootAggregateCheckOnLoad.value) {
        Project.extract(state2).runTask(sbtRootAggregateCheck, state2)._1
      } else {
        state2
      }
    }
  )
}
