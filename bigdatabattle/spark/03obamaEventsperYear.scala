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

// Count Obama events per year

val df = sqlContext.sql("""
SELECT year, COUNT(globaleventid) AS nb_events FROM gdelt.eventsorc
WHERE actor1name='BARACK OBAMA'
GROUP BY year
ORDER BY year ASC
""")

time {
  df.collect()
}

