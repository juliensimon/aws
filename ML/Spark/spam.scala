import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.configuration.BoostingStrategy

// Load 2 types of emails from text files: spam and ham (non-spam).
// Each line has text from one email.
//sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", "XXXZZZHHH")
//sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", "xxxxxxxxxxxxxxxxxxxxxxxxxxx")
//val spam = sc.textFile("s3://jsimon-public/spam")
//val ham = sc.textFile("s3://jsimon-public/ham")
val spam = sc.textFile("file:///Users/julsimon/dev/aws/ML/Spark/spam")
val ham = sc.textFile("file:///Users/julsimon/dev/aws/ML/Spark/ham")

// Create a HashingTF instance to map email text to vectors of 1000 features.
val tf = new HashingTF(numFeatures = 1000)
// Each email is split into words, and each word is mapped to one feature.
val spamFeatures = spam.map(email => tf.transform(email.split(" ")))
val hamFeatures = ham.map(email => tf.transform(email.split(" ")))

// Create LabeledPoint datasets for positive (spam) and negative (ham) examples.
val positiveExamples = spamFeatures.map(features => LabeledPoint(1, features))
val negativeExamples = hamFeatures.map(features => LabeledPoint(0, features))

val data = positiveExamples.union(negativeExamples)
data.cache()
// Split the data set 80/20
val Array(trainingData, testData) = data.randomSplit(Array(0.8, 0.2))
trainingData.cache()

// https://spark.apache.org/docs/2.2.0/mllib-linear-methods.html#classification
val numIterations = 100
val modelSVM = SVMWithSGD.train(trainingData, numIterations)

val predictionLabel = testData.map(x=> (modelSVM.predict(x.features),x.label))
val accuracy = 1.0 * predictionLabel.filter(x => x._1 == x._2).count() / testData.count()

// https://spark.apache.org/docs/2.2.0/mllib-linear-methods.html#classification
// Create a Logistic Regression model which uses the LBFGS optimizer.
val lr = new LogisticRegressionWithLBFGS()
// Train learning algorithm on the training data.
val modelLR = lr.run(trainingData)

val predictionLabel = testData.map(x=> (modelLR.predict(x.features),x.label))
val accuracy = 1.0 * predictionLabel.filter(x => x._1 == x._2).count() / testData.count()

// https://spark.apache.org/docs/2.2.0/mllib-decision-tree.html
val numClasses = 2
val categoricalFeaturesInfo = Map[Int, Int]()
val numTrees = 16
val featureSubsetStrategy = "auto"
val impurity = "gini"
val maxDepth = 16
val maxBins = 16

val modelRF = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
  numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

val predictionLabel = testData.map(x=> (modelRF.predict(x.features),x.label))
val accuracy = 1.0 * predictionLabel.filter(x => x._1 == x._2).count() / testData.count()

// https://spark.apache.org/docs/2.2.0/mllib-ensembles.html#gradient-boosted-trees-gbts
// Train a GradientBoostedTrees model.
// The defaultParams for Classification use LogLoss by default.
val boostingStrategy = BoostingStrategy.defaultParams("Classification")
boostingStrategy.numIterations = 10
boostingStrategy.treeStrategy.numClasses = 2
boostingStrategy.treeStrategy.maxDepth = 5
// Empty categoricalFeaturesInfo indicates all features are continuous.
boostingStrategy.treeStrategy.categoricalFeaturesInfo = Map[Int, Int]()

val modelGBT = GradientBoostedTrees.train(trainingData, boostingStrategy)

val predictionLabel = testData.map(x=> (modelGBT.predict(x.features),x.label))
val accuracy = 1.0 * predictionLabel.filter(x => x._1 == x._2).count() / testData.count()

// https://spark.apache.org/docs/2.2.0/mllib-naive-bayes.html
val modelBayes = NaiveBayes.train(trainingData, 1.0)

val predictionLabel = testData.map(x=> (modelBayes.predict(x.features),x.label))
val accuracy = 1.0 * predictionLabel.filter(x => x._1 == x._2).count() / testData.count()

val posTestExample = tf.transform("You have won $1,000,000. Please fly to Nigeria ASAP".split(" "))
val negTestExample = tf.transform("Hi Mom, I started studying Spark the other day, it's awesome".split(" "))

modelBayes.predict(posTestExample)
modelBayes.predict(negTestExample)
println(s"Prediction for positive test example: ${modelBayes.predict(posTestExample)}")
println(s"Prediction for negative test example: ${modelBayes.predict(negTestExample)}")

