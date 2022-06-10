import warnings

from repository import connect, tuple2df

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
from sklearn.metrics import accuracy_score

import seaborn as sns
import matplotlib.pyplot as plt

from xgboost import XGBClassifier, XGBRegressor

import tensorflow as tf
from tensorflow import keras

from keras.models import Sequential
from keras.layers import Dense

import numpy as np


####################################
warnings.filterwarnings('ignore')
plt.ion()
pd.set_option('display.max_columns', None)
####################################



# Connection parameters, yours will be different
param_dic = {
    "host"      : "localhost",
    "database"  : "vat-evasion",
    "user"      : "postgres",
    "password"  : "postgresql"
}

# connect to db
conn = connect(param_dic)

# 1:20 ratio
df = tuple2df(conn, 
     """
     drop table if exists tbl;
     create temporary table if not exists tbl as select * from criterion where label = 0;
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
       	, criteria_19
       	, criteria_20
       	, criteria_21
       	, criteria_22
       	, criteria_23
       	, criteria_24
       	, criteria_25
       	, criteria_26
       	, criteria_27
       	, criteria_28
       	, criteria_29
       	, criteria_30               
       	, criteria_31	
       	, label
    from (
    	select * from criterion where label = 1
    	union
    	select * from tbl TABLESAMPLE SYSTEM_ROWS((select count(0) from criterion where label = 1) * 20)	
    ) t
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
      "criteria_19",
      "criteria_20",
      "criteria_21",
      "criteria_22",
      "criteria_23",
      "criteria_24",
      "criteria_25",
      "criteria_26",
      "criteria_27",
      "criteria_28",
      "criteria_29",
      "criteria_30",
      "criteria_31",      
      "label"
    ]
)

# no null values found
print(df.isnull())

# show label ratio
df.drop('tin', axis=1, inplace=True)
sns.countplot(df['label'])
plt.show()

# Upsampling using resampling method
df_majority = df[(df['label']==0)]
df_minority = df[(df['label']==1)]

# upsample minority class
df_minority_upsampled = resample(df_minority, 
                                 replace = True
                                 , n_samples = df_majority.shape[0]
                                 )  

df_upsampled = pd.concat([df_minority_upsampled, df_majority])
sns.countplot(df_upsampled['label'])
plt.show()
df = df_upsampled

# Correlation analysis - remove highly multicollinear columns
X = df
y = df['label']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)
corr_matrix = X_train.corr()
f, ax = plt.subplots(figsize=(25, 20))
ax.set_title('Correlation matrix before feature removal')
sns.heatmap(corr_matrix, vmin=-1, vmax=1, square=True, ax=ax, annot=True)
plt.show()

X.drop([
    'criteria_2', 'criteria_3', 'criteria_5', 'criteria_6', 'criteria_16',
    'criteria_17', 'criteria_23', 'criteria_25', 'criteria_30', 'criteria_31'
], axis=1, inplace=True)
X.drop([
    'criteria_8', 'criteria_27', 'criteria_28'
], axis=1, inplace=True)
X.drop(['criteria_21'], axis=1, inplace=True)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)
corr_matrix = X_train.corr()
f, ax = plt.subplots(figsize=(25, 20))
ax.set_title('Correlation matrix after feature removal')
sns.heatmap(corr_matrix, vmin=-1, vmax=1, square=True, ax=ax, annot=True)
plt.show()

X.drop(['label'], axis=1, inplace=True)

# prove feature selection to be true
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
clf_rf = RandomForestClassifier(random_state=43)
clr_rf = clf_rf.fit(X_train, y_train)
ac = accuracy_score(y_test, clf_rf.predict(X_test))
print('Accuracy is: ', ac)
cm = confusion_matrix(y_test, clf_rf.predict(X_test))
sns.heatmap(cm, annot=True, fmt="d")


# Custom Keras MLP implementation
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33)

model = Sequential()
# input layer
model.add(Dense(16, activation='tanh', input_shape=(X.shape[1],)))

# hidden layers
model.add(Dense(8, activation='tanh'))
model.add(Dense(8, activation='tanh'))
model.add(Dense(8, activation='tanh'))

# output layer
model.add(Dense(1, activation='sigmoid'))

model.compile(loss='binary_crossentropy', optimizer='sgd', metrics=['accuracy'])
model.fit(X_train, y_train, epochs=50, batch_size=8, verbose=1, shuffle=True, validation_data=(X_test, y_test))

keras.utils.plot_model(model, to_file='./model.png', show_shapes=True,)
y_pred = model.predict(X_test)
score = model.evaluate(X_test, y_test,verbose=1)
model.save('./keras_mlp')
print(score)

# =============================================================================
# a = np.array([
#     [
#         0, 0, 0,
#         0, 0, 0, 
#         0, 0, 1, 
#         0, 0
#     ],
#     [
#         1, 0, 1,
#         0, 1, 0, 
#         1, 0, 1, 
#         0, 1
#     ],
#     [
#         0, 1, 0,
#         1, 1, 1, 
#         0, 1, 0, 
#         0, 1
#     ],
#     [
#         1, 0, 1,
#         1, 0, 1, 
#         1, 0, 1, 
#         1, 0
#     ]
# ])
# 
# y_pred = model.predict(a)
# print(np.where(y_pred >= .5, 1, 0))
# =============================================================================


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

# =============================================================================
# for i in range(len(model_pipeline)):
#     m = model_pipeline[i].fit(X_train, y_train)
#     y_pred = m.predict(X_test)
#     print("X=%s, Predicted=%s, model=%s" % (X_test[0], y_pred[0], model_list2[i]))   
# =============================================================================
