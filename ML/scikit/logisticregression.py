from sklearn import datasets
from sklearn import metrics
from sklearn.linear_model import LogisticRegression

# Load the iris dataset (1936!)- https://archive.ics.uci.edu/ml/datasets/iris
# 150 samples for 3 different types of irises (Setosa, Versicolour and Virginica)
# The rows are the samples and the columns are: Sepal Length, Sepal Width, Petal Length and Petal Width.
dataset = datasets.load_iris()

print(dataset.data.shape)
print(dataset.data[:10])
print(dataset.target.shape)
print(dataset.target[:10])

# Fit a logistic regression model to the data
model = LogisticRegression()
model.fit(dataset.data, dataset.target)

# Save model for future use
from sklearn.externals import joblib
joblib.dump(model, 'irismodel.pkl')

# Make predictions
expected = dataset.target
predicted = model.predict(dataset.data)

# Display metrics
# Precision measures the impact of false positives: TP/(TP+FP)
# Recall measures the impact of false negatives : TP/(TP+FN)
# F1 is the weighted average of precision and recall: (2*Recall*Precision)/(Recall+Precision)
print(metrics.classification_report(expected, predicted))

# Display confusion matrix
print(metrics.confusion_matrix(expected, predicted))
