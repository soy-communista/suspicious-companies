import warnings
warnings.filterwarnings('ignore')

import psycopg2
import pandas as pd

from sklearn.utils import resample
from imblearn.over_sampling import SMOTE

from sklearn.model_selection import train_test_split
from sklearn import preprocessing

from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier

from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB

from sklearn import metrics
from sklearn.metrics import classification_report
from sklearn.metrics import confusion_matrix

import seaborn as sns
import matplotlib.pyplot as plt

from xgboost import XGBClassifier, XGBRegressor

import tensorflow as tf
from tensorflow import keras

from keras.models import Sequential
from keras.layers import Dense

import numpy as np

# Connection parameters, yours will be different
param_dic = {
    "host"      : "localhost",
    "database"  : "vat-evasion",
    "user"      : "postgres",
    "password"  : "postgresql"
}

def connect(params_dic):
    """ Connect to the PostgreSQL database server """
    conn = None
    try:
        # connect to the PostgreSQL server
        print('Connecting to the PostgreSQL database...')
        conn = psycopg2.connect(**params_dic)
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
        #sys.exit(1) 
    print("Connection successful")
    return conn

def postgresql_to_dataframe(conn, select_query, column_names):
    """
    Tranform a SELECT query into a pandas dataframe
    """
    cursor = conn.cursor()
    try:
        cursor.execute(select_query)
    except (Exception, psycopg2.DatabaseError) as error:
        print("Error: %s" % error)
        cursor.close()
        return 1
    
    # Naturally we get a list of tupples
    tupples = cursor.fetchall()
    cursor.close()
    
    # We just need to turn it into a pandas dataframe
    df = pd.DataFrame(tupples, columns=column_names)
    return df

# Connect to the database
conn = connect(param_dic)
# Execute the "SELECT *" query
df = postgresql_to_dataframe(conn, 
     """
         select
           	  tin
           	, criteria_1
           	, criteria_2
           	, criteria_3
           	, criteria_4
           	, criteria_5
           	, criteria_6
           	, criteria_7
           	, criteria_8
           	, criteria_9
           	, criteria_10
           	, criteria_11
           	, criteria_12
           	, criteria_13
           	, criteria_14
           	, criteria_15
           	, criteria_16
           	, criteria_17
           	, criteria_18
           	, criteria_30
           	, criteria_31	
           	, label
        from labeled_companies
    """, 
    [
      "tin",
      "criteria_1",
      "criteria_2",
      "criteria_3",
      "criteria_4",
      "criteria_5",
      "criteria_6",
      "criteria_7",
      "criteria_8",
      "criteria_9",
      "criteria_10",
      "criteria_11",
      "criteria_12",
      "criteria_13",
      "criteria_14",
      "criteria_15",
      "criteria_16",
      "criteria_17",
      "criteria_18",
      "criteria_30",
      "criteria_31",      
      "label"
    ]
 )

plt.ion()
pd.set_option('display.max_columns', None)

# Drop null columns
drop_columns = ['criteria_3', 'criteria_5', 'criteria_16', 'criteria_30', 'criteria_31']

for drop_column in drop_columns:
    df.drop(drop_column, axis=1, inplace=True)

# Fill average value to null columns
df.fillna(df.mean(), inplace=True)

# Cast to int column values
df = (df.set_index(['tin'], append=True)
        .round(decimals=0)
        .astype(int)
        .reset_index(['tin'])
       )

# drop id column
df.drop('tin', axis=1, inplace=True)

sns.countplot(df['label'])

plt.show()

# Upsampling using resampling method
# method 1
#create two different dataframe of majority and minority class 
df_majority = df[(df['label']==0)] 
df_minority = df[(df['label']==1)] 
# upsample minority class
df_minority_upsampled = resample(df_minority, 
                                 replace=True    # sample with replacement
                                 , n_samples= 29935 # to match majority class
                                 , random_state=42    # # reproducible results
                                 )  
# Combine majority class with upsampled minority class
df_upsampled = pd.concat([df_minority_upsampled, df_majority])

print(df_upsampled['label'].value_counts())
sns.countplot(df_upsampled['label'])

plt.show()

df = df_upsampled

# =============================================================================
# # Upsampling using SMOTE
# # method 2
# # Resampling the minority class. The strategy can be changed as required.
# sm = SMOTE(sampling_strategy='minority')
# # Fit the model to generate the data.
# oversampled_X, oversampled_Y = sm.fit_sample(df.drop('label', axis=1), df['label'])
# oversampled = pd.concat([pd.DataFrame(oversampled_Y), pd.DataFrame(oversampled_X)], axis=1)
# 
# print(oversampled['label'].value_counts())
# sns.countplot(oversampled['label'])
# 
# df = oversampled
# =============================================================================

# Correlation analysis - remove highly multicollinear columns
X = df
y = df['label']

X.drop(['criteria_6'], axis=1, inplace=True)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33)


corr_matrix = X_train.corr()
f, ax = plt.subplots(figsize=(12,9))
sns.heatmap(corr_matrix, vmax=0.9, square=True, ax=ax, annot=True)
plt.show()

X.drop(['label', 'criteria_7', 'criteria_8', 'criteria_13'], axis=1, inplace=True)


# Calculate feature importance

# =============================================================================
# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=.33)
# 
# # XGBoost
# # =============================================================================
# # # method 1
# # model = XGBClassifier()
# # model.fit(X_train, y_train)
# # importance = model.feature_importances_
# # for i,v in enumerate(importance):
# # 	print('Feature: %0d, Score: %.5f' % (i,v))
# # plt.bar([x for x in range(len(importance))], importance)
# # plt.show()
# # =============================================================================
# 
# 
# # =============================================================================
# # # method 2
# # # define the model
# # model = XGBRegressor()
# # # fit the model
# # model.fit(X_train, y_train)
# # # get importance
# # importance = model.feature_importances_
# # # summarize feature importance
# # for i,v in enumerate(importance):
# # 	print('Feature: %0d, Score: %.5f' % (i,v))
# # # plot feature importance
# # plt.bar([x for x in range(len(importance))], importance)
# # plt.show()
# # 
# # 
# # X = df.loc[:, ['criteria_2', 'criteria_7', 'criteria_8', 'criteria_9', 'criteria_13']]
# # =============================================================================
# =============================================================================


X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33)


model = Sequential()
model.add(Dense(8, activation='relu', input_shape=(11,)))
model.add(Dense(8, activation='relu'))
model.add(Dense(1, activation='sigmoid'))

model.compile(loss='binary_crossentropy',
optimizer='sgd',
metrics=['accuracy'])
model.fit(X_train, y_train, epochs=4, batch_size=1, verbose=1)

keras.utils.plot_model(model, to_file='/tmp/model.png', show_shapes=True,)
y_pred = model.predict(X_test)
score = model.evaluate(X_test, y_test,verbose=1)
print(score)

a = np.array([
    [
        0, 0, 0,
        0, 0, 0, 
        0, 0, 1, 
        0, 0
    ],
    [
        1, 0, 1,
        0, 1, 0, 
        1, 0, 1, 
        0, 1
    ],
    [
        0, 1, 0,
        1, 1, 1, 
        0, 1, 0, 
        0, 1
    ],
    [
        1, 0, 1,
        1, 0, 1, 
        1, 0, 1, 
        1, 0
    ]
])

y_pred = model.predict(a)
print(np.where(y_pred > .5, 1, 0))

'''
basic_model = Sequential()
basic_model.add(Dense(units = 16 , activation = 'sigmoid', input_shape = (11,)))
basic_model.add(Dense(1, activation = 'hard_sigmoid'))

sgd = keras.optimizers.SGD(lr=0.5, momentum=0.9, nesterov=True)
basic_model.compile(loss = 'binary_crossentropy', optimizer = 'sgd', metrics = ['accuracy'])
basic_model.fit(X_train, y_train, epochs=5)
loss_and_metrics = basic_model.evaluate(X_test, y_test)
print('Loss = ',loss_and_metrics[0])
print('Accuracy = ',loss_and_metrics[1])

a = np.array([
    [
        0, 0, 0,
        0, 0, 0, 
        0, 0, 1, 
        0, 0
    ],
    [
        1, 0, 1,
        0, 1, 0, 
        1, 0, 1, 
        0, 1
    ],
    [
        1, 0, 1,
        1, 0, 1, 
        1, 0, 1, 
        1, 0
    ],
    [
        0, 0, 0,
        0, 0, 0, 
        0, 0, 0, 
        0, 0
    ]
])
print(keras.utils.to_categorical(basic_model.predict(a)))
'''

'''
model = keras.Sequential([
    keras.layers.Flatten(input_shape=(11,)),
    keras.layers.Dense(16, activation=tf.nn.relu),
	keras.layers.Dense(16, activation=tf.nn.relu),
    keras.layers.Dense(1, activation=tf.nn.sigmoid),
])

model.compile(optimizer='adam',
              loss='binary_crossentropy',
              metrics=['accuracy'])

model.fit(X_train, y_train, epochs=5, batch_size=1)

test_loss, test_acc = model.evaluate(X_test, y_test)
print('Test accuracy:', test_acc)

a = np.array([
    [
        0, 0, 0,
        0, 0, 0, 
        0, 0, 1, 
        0, 0
    ],
    [
        1, 0, 1,
        0, 1, 0, 
        1, 0, 1, 
        0, 1
    ],
    [
        0, 1, 0,
        1, 1, 1, 
        0, 1, 0, 
        0, 1
    ],
    [
        1, 0, 1,
        1, 0, 1, 
        1, 0, 1, 
        1, 0
    ]
])
print(model.predict(a))
'''

model_pipeline = []
model_pipeline.append(LogisticRegression(solver='liblinear'))
model_pipeline.append(SVC())
model_pipeline.append(KNeighborsClassifier())
model_pipeline.append(DecisionTreeClassifier())
model_pipeline.append(RandomForestClassifier())
model_pipeline.append(GaussianNB())

model_list = ['Logistic Regression', 'SVM', 'KNN', 'Decision Tree', 'Random Forest', 'Naive Bayes']

model_list2 = ['Logistic Regression', 'SVM', 'KNN', 'Decision Tree', 'Random Forest', 'Naive Bayes']

acc_list = []
prec_list = []
rec_list = []
f1_list = []
auc_list = []
cm_list = []

for model in model_pipeline:
    model.fit(X_train, y_train)
    y_pred = model.predict(X_test)
    
    acc = metrics.accuracy_score(y_test, y_pred)
    prec = metrics.precision_score(y_test, y_pred)
    rec = metrics.recall_score(y_test, y_pred)
    f1 = (2 * prec * rec) / (prec + rec)
    
    acc_list.append(acc)
    prec_list.append(prec)
    rec_list.append(rec)
    f1_list.append(f1)
    
    fpr, tpr, _thresholds = metrics.roc_curve(y_test, y_pred)
    auc_list.append(round(metrics.auc(fpr, tpr), 2))
    cm_list.append(confusion_matrix(y_test, y_pred))

fig = plt.figure(figsize = (18, 10))
for i in range(len(cm_list)):
    cm = cm_list[i]
    model = model_list2[i]
    sub = fig.add_subplot(2, 3, i+1).set_title(model)
    cm_plot = sns.heatmap(cm, annot=True, cmap='Blues_r', fmt='.5g')
    cm_plot.set_xlabel('Predicted values')
    cm_plot.set_ylabel('Actual values')
    
result_df = pd.DataFrame({
    'Model':model_list2, 'Accuracy':acc_list, 'Precision':prec_list, 'Recall':rec_list, 'F1':f1_list, 'AUC':auc_list
})

print(result_df)

#criteria_1, criteria_2, criteria_4, 
#criteria_9, criteria_10, criteria_11, 
#criteria_12, criteria_14, criteria_15, 
#criteria_17, criteria_18

Xnew = [[0, 0, 0, 
         0, 0, 0, 
         0, 0, 1, 
         0, 0]]

for i in range(len(model_pipeline)):
    m = model_pipeline[i].fit(X_train, y_train)
    y_pred = m.predict(Xnew)
    print("X=%s, Predicted=%s, model=%s" % (Xnew[0], y_pred[0], model_list2[i]))        

