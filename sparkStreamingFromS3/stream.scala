import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.streaming.Seconds

Logger.getLogger("org").setLevel(Level.ERROR)
Logger.getLogger("com").setLevel(Level.ERROR)
Logger.getLogger("akka").setLevel(Level.ERROR)

val hadoopConf=sc.hadoopConfiguration;
hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
hadoopConf.set("fs.s3.awsAccessKeyId","ACCESS_KEY")
hadoopConf.set("fs.s3.awsSecretAccessKey","SECRET_KEY")
val ssc = new org.apache.spark.streaming.StreamingContext(sc,Seconds(10))
val lines = ssc.textFileStream("s3n://jsimon-public/firehose2016/04/27/09/")
lines.print()
ssc.start()
