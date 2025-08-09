package sbt_root_aggregate

private[sbt_root_aggregate] object SbtRootAggregateCompat {
  implicit class DefOps(private val self: sbt.Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
