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

// Top 10 events per category
val df = sqlContext.sql("""
SELECT eventcode,
       eventcodes.description,
       nb_events
FROM
  (SELECT eventsparquet.eventcode,
          COUNT(eventsparquet.globaleventid) AS nb_events
   FROM eventsparquet
   GROUP BY eventsparquet.eventcode
   ORDER BY nb_events DESC
   LIMIT 10)
JOIN eventcodes ON eventcode = eventcodes.code
ORDER BY nb_events DESC
""")

time {
  df.collect()
}

