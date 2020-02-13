# -*- coding: utf-8 -*-
# @Time    : 2020/1/29
# @Author  : li
# @FileName: ClassifierJobNameApp..py
# @Desc    :


import os
import jieba
import pickle

from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib
from sklearn import metrics
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.datasets.base import Bunch
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC


class func_tools(object):
    '''
    工具类
    '''
    def __init__(self):
        pass

    def tokenizer(self):
        return jieba

    def readfile(self, file_path, encoding='utf-8'):
        '''
        读取文本文件内容
        :param file_path: str 文本地址
        :param encoding: str 打开文件格式
        :return: str file_path下文本文件内容
        '''
        with open(file_path, "rt", encoding=encoding) as fp:
            content = fp.read()
        return content

    def savefile(self, savepath, content):
        '''
        保存文本到指定路径
        :param savepath: str 保存路径
        :param content: str 文本文件内容
        :return:
        '''
        with open(savepath, "wt") as fp:
            fp.write(content)

    def writeobj(self, path, obj):
        '''
        持久化python对象
        :param path: str 保存bunch文件地址
        :param obj: Bunch 内容
        :return:
        '''
        with open(path, "wb") as file_obj:
            pickle.dump(obj, file_obj)

    def readobj(self, path):
        '''
        载入python对象
        :param path: str 文件地址
        :return: Bunch 文件内容
        '''
        with open(path, "rb") as file_obj:
            obj = pickle.load(file_obj)
        return obj

    def check_dir_exist(self, dir):
        '''
        坚持目录是否存在，不存在则创建
        :return:
        '''
        if not os.path.exists(dir):
            os.mkdir(dir)


class TextClassifier(object):
    '''
    分类器类，可以根据需要选择不同的分类算法
    '''
    def __init__(self, clf_model, space_path, test_space_path, model_path):
        '''
        分类器
        :param clf_model: str 分类器算法模型
        :param space_path: str 训练集特征数据存放位置
        :param test_space_path: str 测试集特征数据存放位置
        :param model_path: str 模型保存路径
        '''
        self.space_path = space_path
        self.test_space_path = test_space_path
        self.model_path = model_path
        self.clf = self._load_clf_model(clf_model)

    def _load_clf_model(self, clf_model):
        '''
        判断是否已有模型
        :param clf_model: str 模型路径
        '''
        if os.path.exists(self.model_path):
            print('loading exists model...')
            return joblib.load(self.model_path)
        else:
            print('training model...')
            train_set = func_tools().readobj(self.space_path)
            clf = clf_model.fit(train_set.tdm, train_set.label)
            joblib.dump(clf, self.model_path)
            return clf

    def validation(self):
        '''
        使用测试集进行模型验证
        '''
        print('starting validation...')
        # 导入测试集特征数据 ./data/jobInfo_app/fearture/testspace.dat
        test_set = func_tools().readobj(self.test_space_path)

        # 预测分类结果
        predicted = self.clf.predict(test_set.tdm)
        for flabel, file_name, category in zip(test_set.label, test_set.filenames, predicted):
            if flabel != category:
                pass
                # print(file_name, ": 实际类别:", flabel, " -->预测类别:", category)

        self.metrics_result(test_set.label, predicted)

    def metrics_result(self, actual, predict):
        '''
        计算分类精度
        '''
        print('精度:{0:.3f}'.format(metrics.precision_score(actual, predict, average='weighted')))
        print('召回:{0:0.3f}'.format(metrics.recall_score(actual, predict, average='weighted')))
        print('f1-score:{0:.3f}'.format(metrics.f1_score(actual, predict, average='weighted')))

    def predict_category(self, text_string, stop_words_path):
        '''
        对输入文件预测分类
        :param text_string: str 预测文本分类
        :param stop_words_path: str 停用词文本路径
        :return predicted_result: str 返回文本分类结果
        '''
        print('*********** 语句 *************')
        input_data = [text_string]
        stpwrdlst = func_tools().readfile(stop_words_path).split()  # 读取停用词

        # 构建tf-idf词向量空间对象
        tfidfspace = Bunch(tdm=[], vocabulary={})

        trainbunch = func_tools().readobj(self.space_path)
        tfidfspace.vocabulary = trainbunch.vocabulary
        vectorizer = TfidfVectorizer(stop_words=stpwrdlst, sublinear_tf=True, max_df=0.5, vocabulary=trainbunch.vocabulary)
        tfidfspace.tdm = vectorizer.fit_transform(input_data)

        # 预测分类结果
        predicted = self.clf.predict(tfidfspace.tdm)
        # 打印输出
        predicted_result = ''
        for sentence, category in zip(input_data, predicted):
            predicted_result = str(category)
            print('Input:', sentence, '\nPredicted category:', category)

        return predicted_result


class Application(object):
    '''
    应用
    '''
    def __init__(self):
        ''' 目录(文件夹)后缀: _dir   文件后缀: _path '''
        # 训练集和测试集
        self.jobInfo_app = './data/jobInfo_app'
        func_tools().check_dir_exist(dir=self.jobInfo_app)

        # 词袋目录地址
        self.fearture_space_dir = os.path.join(self.jobInfo_app, 'fearture_space')
        func_tools().check_dir_exist(dir=self.fearture_space_dir)
        # 模型存放的文件夹
        self.model_dir = os.path.join(self.jobInfo_app, 'models')
        func_tools().check_dir_exist(dir=self.model_dir)

        # 训练集集进行Bunch化后，词袋文件路径
        self.train_wordbag_path = os.path.join(self.fearture_space_dir, 'train_set.dat')
        # 测试集集进行Bunch化后，词袋文件路径
        self.test_wordbag_path = os.path.join(self.fearture_space_dir, 'test_set.dat')
        # 词向量空间保存路径
        self.space_path = os.path.join(self.fearture_space_dir, 'tfdifspace.dat')
        # TF-IDF词向量空间保存路径
        self.tfidf_space_path = os.path.join(self.fearture_space_dir, 'testspace.dat')
        # 停用词表的路径
        self.stopword_path = "./stop_words/stop_words.txt"

        # 贝叶斯模型地址：./data/jobInfo/models/bayes_NBclassifier.pkl
        self.bayes_model_path = os.path.join(self.model_dir, 'bayes_NBclassifier.pkl')
        # 随机森林模型地址：./data/jobInfo/models/forest_NBclassifier.pkl
        self.forest_model_path = os.path.join(self.model_dir, 'forest_NBclassifier.pkl')
        # Logistic模型地址：./data/jobInfo/models/ogistic_NBclassifier.pkl
        self.logistic_model_path = os.path.join(self.model_dir, 'logistic_NBclassifier.pkl')
        # SVM模型地址：./data/jobInfo/models/SVM_NBclassifier.pkl
        self.SVM_model_path = os.path.join(self.model_dir, 'SVM_NBclassifier.pkl')

        # 训练分类器迭代次数,alpha越小，迭代次数越多，精度越高
        self.alpha_num = 0.00001


    def create_model(self):
        '''
        训练模型
        '''
        # 多项式贝叶斯算法训练模型
        print('--------------- 训练模型 --------------')
        from sklearn.naive_bayes import MultinomialNB

        # 模型存放的文件夹
        func_tools().check_dir_exist(dir=self.model_dir)

        print('\n贝叶斯算法训练模型')
        # 贝叶斯算法训练模型
        # 训练分类器：输入词袋向量和分类标签，alpha:0.001 alpha越小，迭代次数越多，精度越高
        clf = MultinomialNB(alpha=self.alpha_num)
        classifier = TextClassifier(clf_model=clf, space_path=self.space_path, test_space_path=self.tfidf_space_path, model_path=self.bayes_model_path)
        classifier.validation()

        print('\n随机森林算法算法训练模型')
        # 随机森林算法算法训练模型
        clf = RandomForestClassifier(bootstrap=True, oob_score=True, criterion='gini')
        classifier = TextClassifier(clf_model=clf, space_path=self.space_path, test_space_path=self.tfidf_space_path, model_path=self.forest_model_path)
        classifier.validation()

        print('\nLogistic回归模型')
        # Logistic回归模型
        clf = LogisticRegression(C=1000.0)
        classifier = TextClassifier(clf_model=clf, space_path=self.space_path, test_space_path=self.tfidf_space_path, model_path=self.logistic_model_path)
        classifier.validation()

        print('\nSVM模型')
        # SVM模型
        clf = SVC(kernel='linear')
        classifier = TextClassifier(clf_model=clf, space_path=self.space_path, test_space_path=self.tfidf_space_path, model_path=self.SVM_model_path)
        classifier.validation()

    def use_classification(self, text_dir=None, text_string=None, model=None):
        '''
        对输入文本进行分类
        :param text_string: str 对一个字符串文本预测分类
        :param text_dir: str 对文本文档内容分类
        :param model: str 使用模型选择 model = 'bayes' 'forset' 'logistic' 'SVM'
        :return : str 返回预测类别结果
        '''
        if model == 'bayes':
            model_path = self.bayes_model_path
        elif model == 'forest':
            model_path = self.forest_model_path
        elif model == 'logistic':
            model_path = self.logistic_model_path
        elif model == 'SVM':
            model_path = self.SVM_model_path
        else:
            model = 'SVM'
            model_path = self.SVM_model_path
        print('\n采用', model)

        if text_string:
            seg_text_string = " ".join(jieba.cut(text_string, cut_all=False))
            from sklearn.naive_bayes import MultinomialNB
            classifier = TextClassifier(clf_model=MultinomialNB(alpha=self.alpha_num), space_path=self.space_path,
                                        test_space_path=self.tfidf_space_path, model_path=model_path)
            ret = classifier.predict_category(text_string=seg_text_string, stop_words_path=self.stopword_path)
            return ret
        elif text_dir:
            print("*****************  目录  *******************")
            print(text_dir)
        else:
            return None



if __name__ == "__main__":

    app = Application()

    print('--------------- 模型生成 --------------')
    app.create_model()

    print('--------------- 模型应用 --------------')
    # model = 'bayes' 'forset' 'logistic' 'SVM'
    ret = app.use_classification(text_string='熟悉软件开发流程，熟练掌握SVN、Maven等开发和协同工具', model='bayes')
    print('return:', ret)
    ret = app.use_classification(text_string='维护数据库结构及数据的完整、一致性', model='forset')
    print('return:', ret)
    ret = app.use_classification(text_string='精通至少一门以下语言：Go，Python，Java，C/C++，C# 根据项目需要，有兴趣并有能力学习go语言', model='logistic')
    print('return:', ret)
    ret = app.use_classification(text_string='理解力强，善于沟通，团队合作意识强', model='SVM')
    print('return:', ret)
    ret = app.use_classification(text_string='但是看见你吃看就啊在开车呢')
    print('return:', ret)



