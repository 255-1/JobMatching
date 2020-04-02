
# coding: utf-8

# # 使用包

# In[1]:


import numpy as np
import pandas as pd
import jieba
import re
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import f1_score,precision_score,recall_score,accuracy_score
import sklearn
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn import svm
import itertools
import collections
import array
import os
from sklearn.model_selection import cross_val_score
from sklearn.externals import joblib


# # 模块

# In[2]:


def sen2words(f_text,f_stopwords,f_addwords):
    df=pd.read_excel(f_text)

    jieba.load_userdict(f_addwords)
    words=list()
    for sentence in df.iloc[:,0].values:
        word=jieba.lcut(sentence)
        words.append(word)
    df.iloc[:,0]=words
    
    with open(f_stopwords,'r+') as f:
        stopwords=[ i.strip('\n') for i in f.readlines()]
    for i in np.arange(len(df.iloc[:,0])):
        buff_str=list()
        for j in df.iloc[:,0][i]:
            if j not in stopwords and j!='\xa0':
                buff_str.append(j)

        df.iloc[:,0][i]=buff_str
    
    return df
    
    


# In[3]:


def get_words_id(df,f_id):
    freq_counter=collections.Counter(itertools.chain(*(df.iloc[:,0])))
    freq_counter=sorted(freq_counter.items(),key=lambda x:x[1],reverse=True)
    
    words,_=zip(*(filter(lambda x:x[1]>=2,freq_counter)))
    
    print('一共有{0}个词'.format(len(words)))
    
    words_id=dict(zip(words,range(len(words))))
    
    try:
        with open(f_id,'w') as f:
            for i in words_id.items():
                i=list(i)
                i.append(str(i[1]))
                i.append('\n')
                i[1]='-'

                f.writelines(i)
    except Error:
        print("id文件写入失败了...")
        return 
    
    print("id文件写入完成")
    return words_id


# In[4]:


def load_words_id(f_id):
    with open(f_id,'r') as f: 
        ids=dict()
        for i in f.readlines():
            try:
                i=i.replace('\n','')
                temp=i.split('-')
                temp[1]=int(temp[1])
                ids[temp[0]]=temp[1]
            except IndexError:
                continue
    return ids


# In[5]:


def word2vec(df,word_id):
    X=list()
    for t in df:
        vect=array.array('l',[0]*len(word_id))
        for word in t:
            if word not in word_id:
                continue
            vect[word_id[word]]=1
        X.append(vect)
    return X


# In[6]:


def get_score(y_test,y_pred):
    
    f1=f1_score(y_test,y_pred,average='macro')
    p=precision_score(y_test,y_pred,average='macro')
    r=recall_score(y_test,y_pred,average='macro')
    a=accuracy_score(y_test,y_pred)
    
    print('f1:{0}'.format(f1))
    print('p:{0}'.format(p))
    print('r:{0}'.format(r))
    print('a:{0}'.format(a))


# In[7]:


def transform(i):
    if all(i == [1,0,0]):
        j=1
    elif all(i == [0,1,0]):
        j=2
    else:
        j=3
    return j


# In[8]:


def get_classified_sen(X_pred,y_pred):
    y=pd.DataFrame(y_pred,columns=['类别']).reset_index(drop=True)
    X=pd.DataFrame(X_pred,columns=['句子']).reset_index(drop=True)
    result=pd.concat([X,y],axis=1)
    
    classified_sen=list()
    for i in np.arange(1,4):
        classified_sen.append(result[result['类别']==i])
        
    return classified_sen


# # 训练模型

# In[285]:



# In[90]:


def add_new_stopwords(df,f_stopwords):
    d={}
    mode=re.compile(r'^[a-z]+',re.I)
    for i in df.iloc[:,0].values:
        for j in i:
            x=mode.match(j)
            if x == None and len(j) == 1:
                d[j]=d.get(j,0)+1

    d=sorted(d.items(),key=lambda x:x[1],reverse=True)
    d=pd.DataFrame(d)
    
    with open(f_stopwords,'a') as f:
        for i in d.values:
            f.write(str(i[0][0])+"\n")


# In[8]:






def get_word_freq(df):
    dic=collections.Counter(itertools.chain(*(df)))
    dic=sorted(dic.items(),key=lambda x:x[1],reverse=True)
    
    return dic



def sen2words_test(sen,f_stopwords,f_addwords):
    
    jieba.load_userdict(f_addwords)
    words=list()
    for sentence in df.iloc[:,0].values:
        word=jieba.lcut(sentence)
        words.append(word)
    df.iloc[:,0]=words
    
    with open(f_stopwords,'r+') as f:
        stopwords=[ i.strip('\n') for i in f.readlines()]
    for i in np.arange(len(df.iloc[:,0])):
        buff_str=list()
        for j in df.iloc[:,0][i]:
            if j not in stopwords and j!='\xa0':
                buff_str.append(j)

        df.iloc[:,0][i]=buff_str
    
    return df

def sen_split(df):
    result=list()
    for i in df.values:
        temp=re.split('[；。]+',i[0])
        result.append(temp)
        
    mode = re.compile('[a-z1-9][、.．）)]+')
    mode2=re.compile('[\\xa0]+')
    mode3=re.compile('.*：')
    
    for i in result:
        for j in np.arange(len(i)):
            i[j]=mode.sub('',i[j])
            i[j]=mode2.sub('',i[j]).strip()
            i[j]=mode3.sub('',i[j]).strip()

    final=pd.DataFrame()

    for i in result:
        temp=pd.DataFrame(i)
        final=pd.concat([temp,final],axis=0)
    return final


