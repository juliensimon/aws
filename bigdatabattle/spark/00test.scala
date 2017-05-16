import org.apache.spark.sql._

val sqlContext = new SQLContext(sc)
val df = sqlContext.sql("use gdelt")

val df = sqlContext.sql("show tables")
df.collect()
