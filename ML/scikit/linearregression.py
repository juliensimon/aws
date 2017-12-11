import matplotlib.pyplot as plt
import numpy as np
from sklearn import linear_model
from sklearn.metrics import mean_squared_error, r2_score

X_train = np.array([1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
Y_train = np.array([1, 2.5, 2, 3, 5, 4, 5.5, 7, 6, 8])

# Transform vectors into 10-line, 1-column matrices
X_train = X_train.reshape((-1, 1))
Y_train = Y_train.reshape((-1, 1))

# Create linear regression object
#regr = linear_model.LinearRegression()
regr = linear_model.SGDRegressor()

# Train the model
regr.fit(X_train, Y_train)

# Save model for future use
from sklearn.externals import joblib
joblib.dump(regr, 'linearregressionmodel.pkl')

# Print coefficient and intercept
print("Coefficient: %s " % regr.coef_)
print("Intercept: %s " % regr.intercept_)

# Make predictions
Y_pred = regr.predict(X_train)

# Print variance and RMSE
print("Variance: %s " % r2_score(Y_train, Y_pred))
print("RMSE: %s " % mean_squared_error(Y_train, Y_pred))

# Plot outputs
plt.scatter(X_train, Y_train,  color='blue')
plt.scatter(X_train, Y_pred, color='orange')
plt.plot(X_train, Y_pred, color='orange', linewidth=1)

plt.show()
