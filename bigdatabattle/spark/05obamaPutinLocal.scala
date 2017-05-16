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


// Count Obama/Putin and Putin/Obama events per category

val df = sqlContext.sql("""
WITH tmp as (SELECT eventsparquetlocal.eventcode,
         COUNT(eventsparquetlocal.globaleventid) AS nb_events
    FROM eventsparquetlocal
    WHERE ((actor1name LIKE '%OBAMA' and actor2name LIKE '%PUTIN')
            OR (actor2name LIKE '%OBAMA' and actor1name LIKE '%PUTIN'))
    GROUP BY  eventsparquetlocal.eventcode
    ORDER BY  nb_events DESC)
SELECT eventcode, eventcodes.description, nb_events
FROM tmp
JOIN eventcodes
    ON eventcode = eventcodes.code
ORDER BY  nb_events DESC
""")

time {
  df.collect()
}

