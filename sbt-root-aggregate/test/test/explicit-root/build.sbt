val a1 = project
val a2 = project
val a3 = project

val root = project.in(file(".")).aggregate(a1, a2)

ThisBuild / sbtRootAggregateExclude += "a3"
