

import numpy as np
import pandas as pd
import jieba
import re
from sklearn.model_selection import train_test_split
from sklearn.metrics import f1_score,precision_score,recall_score,accuracy_score
import sklearn
from sklearn import svm
import itertools
import collections
import array
import os
from sklearn.model_selection import cross_val_score
from sklearn.externals import joblib
# import joblib 如果你的sklearn版本大于0.21,你需要单独安装这个包



class JobinfoSpliter:
    '''
        Constructor:
                    JobinfoSpliter(jobinfo,stopwords,addwords)
                    
                    jobinfo   DataFrame
                              分类所使用大文本
                    stopwords 停用词txt文件 一行一个词
                    addwords  增加词txt文件 一行一个词
                   
                    
        Attributes:
                    jobinfo DataFrame
                            需要进行分类的jobinfo
                    sen     DataFrame
                            jobinfo分裂出来的句子
                    words   DataFrame
                            句子分裂后提取的词
                    stopwords str 
                              stopwords的txt文件地址
                    addwords  str 
                              addwords 的txt文件地址
        Functions:
                    sen_split() 将jobinfo分成多个句子
                    sen2words() 将句子分成多个单词
                    
                    get_words() 调用sen_split(),sen2words()，直接获得分割为词的Dataframe
    '''
    

    def __init__(self,jobinfo,stopwords,addwords):
        self.jobinfo=jobinfo
        self.stopwords=stopwords
        self.addwords=addwords
        self.sen=[]
        self.words=[]
        
    def sen_split(self):
        '''
            将self.jobinfo分裂成多个句子，其结果赋给self.sen,并将结果返回
            
            Parameters: None
            Returns: DataFrame
                     self.jobinfo分裂后的句子
            
        '''
        result=list()
        mode = re.compile('[a-z1-9][、.．）)]+')
        mode2=re.compile('[\\xa0]+')
        mode3=re.compile('[、.．：:（）()]')
        
        try:
            jobinfo=self.jobinfo.values
        except AttributeError as e:
            print("属性jobinfo类型错误,请确保传入的类型为DataFrame。")
        else:           
            for i in jobinfo:
                temp=re.split('[；。，]+',i)
                for j in np.arange(len(temp)):
                    temp[j]=mode.sub('',temp[j])
                    temp[j]=mode2.sub('',temp[j]).strip()
                    temp[j]=mode3.sub('',temp[j]).strip()
                result.append(temp)

            sen=pd.DataFrame()

            for i in result:
                temp=pd.DataFrame(i)
                sen=pd.concat([temp,sen],axis=0)
            self.sen=sen.reset_index(drop=True)
            return sen
        
    def sen2words(self):
        '''
            将self.sen分裂成词，其结果赋给self.words,并将结果返回
            
            Parameters: None
            Returns: DataFrame
                     self.sen分裂出来的词
        '''
        try:
            sen=self.sen.iloc[:,0].values
            df=pd.DataFrame(np.zeros(self.sen.shape[0]))
        except AttributeError as e:
            print("属性sen类型错误,请在使用本方法前使用sen_split()或是使用get_words()直接得到结果。")
        else:
            with open(self.stopwords,'r+',encoding='utf8') as f:
                stopwords=[ i.strip('\n') for i in f.readlines()]

            jieba.load_userdict(self.addwords)

            words=list()

            for sentence in sen:
                word=jieba.lcut(sentence)
                temp=list()
                for w in word:
                    if w not in stopwords and w!='\xa0':
                        temp.append(w)
                words.append(temp)
            df.iloc[:,0]=words

            self.words=df.reset_index(drop=True)

            return df

    def get_words(self):
        '''
            获得分割为词的Dataframe
            
            Parameters: None
            Returns: DataFrame
                     分割成词的结果
        '''
        self.sen_split()
        self.sen2words()
        
        result = self.words
        return result
    




class JobinfoClassifier(JobinfoSpliter):
    '''
        Constructor:
                    JobClassifier(model,jobinfo,stopwords,addwords,words_id)
                    
                    model     .model结尾文件
                              sklearn训练完成的模型
                    jobinfo   DataFrame
                              分类所使用大文本
                    stopwords 停用词txt文件 一行一个词
                    addwords  增加词txt文件 一行一个词
                    words_id  词向量id文件  每一行为 Word-id(如 前端-1)
                   
                    
        Attributes:
                    model   sklearn.svm.classes.SVC
                            导入的模型
                    words_id str 
                             词id的txt文件地址
                    jobinfo DataFrame
                            需要进行分类的jobinfo
                    sen     DataFrame
                            jobinfo分裂出来的句子
                    words   DataFrame
                            句子分裂后提取的词
                    stopwords str 
                              stopwords的txt文件地址
                    addwords  str 
                              addwords的txt文件地址
                    ids     dict
                            词id字典 key:词，value:id
                    vec     list
                            词向量
                    result  DataFrame
                            模型分类的结果，一共3列，由[句子,分割后的词，分类]组成。
                            分别对应self.sen,self.words,分类结果
        
        Functions:
                    sen_split() 将jobinfo分成多个句子
                    sen2words() 将句子分成多个单词
                    word2vec()  将单词化为词向量
                    
                    load_words_id() 读取词向量的id文件
                    predict()       预测输入的文本的类别
                    get_words_frequency() 获得词分类后,每一个类别中不同词汇的出现频率
    '''
    def __init__(self,model,jobinfo,stopwords,addwords,words_id):
        super().__init__(jobinfo,stopwords,addwords)
        self.model=joblib.load(model)
        self.words_id=words_id
        self.ids=[] 
        self.vec=[] 
        self.result=[]
        
    def load_words_id(self):
        '''
            读取词ID文件,其结果赋给self.ids,并返回结果
            
            Parameters: None
            Returns: dict
                     self.ids key:词 values:id
        '''
        with open(self.words_id,'r',encoding='utf8') as f: 
            ids=dict()
            for i in f.readlines():
                try:
                    i=i.replace('\n','')
                    temp=i.split('-')
                    temp[1]=int(temp[1])
                    ids[temp[0]]=temp[1]
                except IndexError:
                    continue
        self.ids=ids
        
        return ids

    def word2vec(self):
        '''
            将self.words转换成词向量,其结果赋给self.vec,并返回改结果
            
            Parameters: None
            Returns: list
                     self.vec 词向量
        '''
        X=list()
        for t in self.words.values:
            vec=array.array('l',[0]*len(self.ids))
            for word in t[0]:
                if word not in self.ids:
                    continue
                vec[self.ids[word]]=1
            X.append(vec)
        
        self.vec=X
        
        return X
    
    def predict(self):
        '''
            对self.vec进行预测,并将结果返回
            
            Parameters: None
            Returns: DataFrame
                     模型分类的结果，一共3列，由[句子,分割后的词，分类]组成。
                     分别对应self.sen,self.words,分类结果
        '''
        self.load_words_id()
        print('导入词id成功')
        self.sen_split()
        print('句子分裂完成')
        self.sen2words()
        print('词分裂完成')
        self.word2vec()
        print('词向量构建完成')
        
        y_pred=pd.DataFrame(self.model.predict(self.vec))
        
        result=pd.concat([self.sen,self.words,y_pred],axis=1)
        result.columns=['句子','分割后的词','分类']
        result.sort_values(['分类'],inplace=True)
        
        self.result=result
        
        return result
    
    def get_words_frequency(self):
        '''
            对self.result中分类的词进行统计,并将结果返回
            
            Parameters: None
            Returns: list
                     list中包含3个DataFrame，第一个DataFrame代表被分类为1(专业能力)的词的出现频率统计,
                     后面2个DataFrame类似。
                     
        '''
        words_fre=list()
        
        for i in np.arange(1,4):
            dic=dict()
            for words in self.result[ self.result['分类'] == i]['分割后的词'].values:
                for word in words:
                    dic[word]=dic.get(word,0)+1
            dic=sorted(dic.items(),key=lambda x:x[1],reverse=True)
            words_fre.append(pd.DataFrame(dic))
                    
        return words_fre




class JobinfoTrainer():
    '''
        Constructor:
                    JobinfoTrainer(stopwords,addwords,df)
                    
                    stopwords 停用词txt文件 一行一个词
                    addwords  增加词txt文件 一行一个词
                    df        DataFrame
                              default = None
                              模型训练用数据
                   
                    
        Attributes:
                    model      SVC
                            训练后的模型
                    df      DataFrame
                            模型训练用数据
                    stopwords str 
                              stopwords的txt文件地址
                    addwords  str 
                              addwords的txt文件地址
                    ids     dict
                            模型训练学习到的词  key:词，value:id
                    X_train Series
                            模型训练集
                    X_test  Series
                            模型测试集
                    y_train DataFrame
                            模型训练集类别
                    y_test  DataFrame
                            模型测试集类别
                    words_id str
                             学习到的词id文件保存地址    
                    v_X_train list
                              转换为词向量矩阵的训练集，list每个元素为array(词向量)
                    v_X_test list
                             转换为词向量矩阵的测试集，list每个元素为array(词向量)
        
        Functions:
                    transform() 将句子的标签进行转换
                    read_excels_from_dir() 读取文件夹中所有的excel，合并其中的内容
                    sen2words() 将句子化为单词
                    word2vec()  将单词化为词向量
                    get_words_id() 获得训练数据的词id，并把它写入txt文件
                    
                    fit() 训练模型
                    get_score() 查看模型的训练效果
                    dump_model() 将模型导出
    '''
    
    

    def __init__(self,stopwords,addwords,df=None):
        
        self.df=df
        
        if df:
            print('训练用Dataframe已经获得')
        else:
            print('未输入训练用DataFrame,请手动赋予或使用read_excels_from_dir()等来赋予训练集')
        
        self.model=[]
        self.stopwords=stopwords
        self.addwords=addwords
        self.ids=[]
        
        self.X_train=[]
        self.X_test=[]
        self.y_train=[]
        self.y_test=[]
        
        self.v_X_train=[]
        self.v_X_test=[]
        
    def transform(self,i):
        '''
            将训练数据的标签进行转换,如果标签为1,2,3形式则不使用该方法
            
            Parameters: int
                        i
            Returns: int
                     转换后的标签
        '''
        if all(i == [1,0,0]):
            j=1
        elif all(i == [0,1,0]):
            j=2
        else:
            j=3
        return j
    
    def read_excels_from_dir(self,f_dir,tran=True):
        '''
             读取文件夹内的所有excel，并将其合并为一个DataFrame。
             **文件夹内应该只有excel文件**
             
             Parameters: str
                         f_dir 文件夹地址
                         
                         bool
                         tran 是否要使用transform()
             Returns: DataFrame
                      合并excel后的DataFrame
        '''
        files=os.listdir(f_dir)

        dir_name=f_dir+'/'

        df=pd.DataFrame()

        for f in files:
            f=dir_name+f
            t_df=pd.read_excel(f)
            df=pd.concat([df,t_df])
        if tran == True:
            df['类别']=df.iloc[:,1:].apply(self.transform,axis=1)
        df.drop(['专业能力','个人能力','工具使用'],inplace=True,axis=1)
        df=df.reset_index(drop=True)
        
        self.df=df

        return df
    
    def sen2words(self):
        '''
            将训练数据的句子转换为词
            
            Parameters: None
            Returns: DataFrame
                     转变成词后的训练集
        '''
        
        with open(self.stopwords,'r+',encoding='utf8') as f:
            stopwords=[ i.strip('\n') for i in f.readlines()]
    
        jieba.load_userdict(self.addwords)
        words=list()
        for sentence in self.df.iloc[:,0].values:
            word=jieba.lcut(sentence)
            
            temp=list()
            for w in word:
                if w not in stopwords and w!='\xa0':
                    temp.append(w)
            words.append(temp)
        self.df.iloc[:,0]=words
    
        return df
    
    def get_words_id(self,f_id):
        '''
            获得训练数据的词ID
            
            Parameters: str
                        f_id 词id文件保存的地址
            Returns: DataFrame
                     转变成词后的训练集
            
        '''
        freq_counter=collections.Counter(itertools.chain(*(self.df.iloc[:,0])))
        freq_counter=sorted(freq_counter.items(),key=lambda x:x[1],reverse=True)

        words,_=zip(*(filter(lambda x:x[1]>=2,freq_counter)))

        print('一共分得{0}个词'.format(len(words)))

        words_id=dict(zip(words,range(len(words))))

        try:
            with open(f_id,'w',encoding='utf8') as f:
                t=0
                for i in words_id.items():
                    i=list(i)
                    i.append(str(i[1]))
                    i.append('\n')
                    i[1]='-'
                    t=i
                    
                    f.writelines(i)
        except Exception as e:
            print("id文件写入失败了...")
            print(e)
            print(i)
            return 

        print("id文件写入完成")
        self.ids=words_id
        
        return words_id
    
    def word2vec(self,df,word_id):
        '''
            将训练集转换为词向量
            
            Parameters: DataFrame
                        df 需要转换为词向量的训练集
                        dict
                        word_id 模型训练学习到的词  key:词，value:id
                        
            Returns: DataFrame
                     转变成词向量后的训练集
        '''
        X=list()
        for t in df:
            vect=array.array('l',[0]*len(word_id))
            for word in t:
                if word not in word_id:
                    continue
                vect[word_id[word]]=1
            X.append(vect)
        return X
    
    def get_score(self,y_test,y_pred,cv):
        '''
            查看模型训练后的结果
            
            Parameters: array-like
                        y_test 测试集标签
                        
                        array-like
                        y_pred 模型预测的结果
                        
                        int 
                        cv 交叉验证的次数
                        
            Returns: None
            
        '''
    
        f1=f1_score(y_test,y_pred,average='macro')
        p=precision_score(y_test,y_pred,average='macro')
        r=recall_score(y_test,y_pred,average='macro')
        a=accuracy_score(y_test,y_pred)
        scores=cross_val_score(self.model,                self.v_X_train,self.y_train.values.ravel(),cv=cv)

        print('f1:{0}'.format(f1))
        print('p:{0}'.format(p))
        print('r:{0}'.format(r))
        print('a:{0}'.format(a))
        print("交叉验证得分{0}".format(np.mean(scores)))
    
    def fit(self,cv,f_id):
        '''
            训练模型
            
            Parameters: int 
                        cv 交叉验证的次数
                        
                        str
                        f_id 词id文件位置
                        
            Returns: SVC
                     训练完后的模型
            
        '''
        self.get_words_id(f_id)
        self.X_train,self.X_test,self.y_train,self.y_test=train_test_split(                            self.df.iloc[:,0],self.df.iloc[:,1:],test_size=0.2)
        self.v_X_train=self.word2vec(self.X_train,self.ids)
        self.v_X_test=self.word2vec(self.X_test,self.ids)
        
        self.model=svm.SVC(kernel='linear')
        self.model.fit(self.v_X_train,self.y_train.values.ravel())
        
        y_pred=self.model.predict(self.v_X_test)
        
        self.get_score(self.y_test,y_pred,cv)
        
        print('训练完成')
        
        return self.model
    
    def dump_model(self,f_model):
        '''
            将训练完的模型导出
            
            Parameters: str
                        f_model 模型导出的文件名
                        
            Returns: None
        '''
        joblib.dump(self.model,f_model)
        print('模型已经导出')
