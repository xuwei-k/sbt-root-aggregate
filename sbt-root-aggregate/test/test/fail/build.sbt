val a1 = project
val a2 = project
val a3 = project

val root = project.in(file(".")).aggregate(a1)

ThisBuild / sbtRootAggregateExclude += "a3"
ThisBuild / sbtRootAggregateCheckOnLoad := false
