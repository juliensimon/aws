import org.apache.spark.sql._

def time[A](f: => A) = {
  val t1 = System.nanoTime
  val ret = f
  val t2 = System.nanoTime
  println("time: "+(t2-t1)/1e9+"s")
  ret
}

val sqlContext = new SQLContext(sc)
val df = sqlContext.sql("use gdelt")

// Count events

val df = sqlContext.sql("SELECT COUNT(*) FROM gdelt.eventsparquet")

time {
  df.collect()
}

