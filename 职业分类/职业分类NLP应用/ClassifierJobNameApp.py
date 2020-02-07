# -*- coding: utf-8 -*-
# @Time    : 2020/1/29
# @Author  : li
# @FileName: ClassifierJobNameApp..py
# @Desc    :


import os
import jieba
import pickle
from sklearn.externals import joblib
from sklearn import metrics
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.datasets.base import Bunch


class func_tools(object):
    def __init__(self):
        pass

    def tokenizer(self):
        return jieba

    def readfile(self, filepath, encoding='utf-8'):
        '''
        读取文本内容
        :param filepath: 文本地址
        :param encoding: 打开文件格式
        :return: filepath.txt的内容
        '''
        # 读取文本
        with open(filepath, "rt", encoding=encoding) as fp:
            content = fp.read()
        return content

    def savefile(self, savepath, content):
        '''
        保存文本
        :param savepath:
        :param content:
        :return:
        '''
        # 保存文本
        with open(savepath, "wt") as fp:
            fp.write(content)

    def writeobj(self, path, obj):
        # 持久化python对象
        # print(obj)
        with open(path, "wb") as file_obj:
            pickle.dump(obj, file_obj)

    def readobj(self, path):
        # 载入python对象
        with open(path, "rb") as file_obj:
            obj = pickle.load(file_obj)
        return obj

    def check_dir_exist(self, dir):
        # 坚持目录是否存在，不存在则创建
        if not os.path.exists(dir):
            os.mkdir(dir)


class TextClassifier(object):
    def __init__(self, clf_model, space_path, test_space_path, model_path):
        """
        分类器
        :param clf_model:   分类器算法模型
        :param data_dir:    特征数据存放位置
        :param model_path:  模型保存路径
        """
        self.space_path = space_path
        self.test_space_path = test_space_path
        self.model_path = model_path
        self.clf = self._load_clf_model(clf_model)

    def _load_clf_model(self, clf_model):
        '''
        判断是否已有模型
        '''
        if os.path.exists(self.model_path):
            print('\nloading exists model...')
            return joblib.load(self.model_path)
        else:
            print('\ntraining model...')
            train_set = func_tools().readobj(self.space_path)
            clf = clf_model.fit(train_set.tdm, train_set.label)
            joblib.dump(clf, self.model_path)
            return clf

    def validation(self):
        """使用测试集进行模型验证"""
        print('starting validation...')
        # 导入测试集
        test_set = func_tools().readobj(self.test_space_path)

        # 预测分类结果
        predicted = self.clf.predict(test_set.tdm)
        for flabel, file_name, expct_cate in zip(test_set.label, test_set.filenames, predicted):
            if flabel != expct_cate:
                print(file_name, ": 实际类别:", flabel, " -->预测类别:", expct_cate)

        self.metrics_result(test_set.label, predicted)

    def metrics_result(self, actual, predict):
        '''
        计算分类精度
        '''
        print('精度:{0:.3f}'.format(metrics.precision_score(actual, predict, average='weighted')))
        print('召回:{0:0.3f}'.format(metrics.recall_score(actual, predict, average='weighted')))
        print('f1-score:{0:.3f}'.format(metrics.f1_score(actual, predict, average='weighted')))

    def predict_category(self, text_string, stop_words_path):
        print('*********** 语句 *************')
        input_data = [text_string]
        stpwrdlst = func_tools().readfile(stop_words_path).split()  # 读取停用词

        # 构建tf-idf词向量空间对象
        tfidfspace = Bunch(target_name=['a'], label=['b'], filenames=['c'], tdm=[],
                           vocabulary={})

        # trainbunch = func_tools().readobj('./data/jobInfo/fearture_space/tfdifspace.dat')
        trainbunch = func_tools().readobj(self.space_path)
        tfidfspace.vocabulary = trainbunch.vocabulary
        vectorizer = TfidfVectorizer(stop_words=stpwrdlst, sublinear_tf=True, max_df=0.5,
                                         vocabulary=trainbunch.vocabulary)
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
        # 模型地址：./data/jobInfo/models/NBclassifier.pkl
        self.model_path = os.path.join(self.model_dir, 'NBclassifier.pkl')

    def create_model(self):
        # 多项式贝叶斯算法训练模型
        print('--------------- 多项式贝叶斯算法训练模型 --------------')
        from sklearn.naive_bayes import MultinomialNB
        # 训练分类器：输入词袋向量和分类标签，alpha:0.001 alpha越小，迭代次数越多，精度越高
        clf = MultinomialNB(alpha=0.00001)
        # 模型存放的文件夹
        func_tools().check_dir_exist(dir=self.model_dir)
        classifier = TextClassifier(clf_model=clf, space_path=self.space_path, test_space_path=self.tfidf_space_path, model_path=self.model_path)
        classifier.validation()

    def use_classification(self, text_dir=None, text_string=None):
        if text_string:
            seg_text_string = " ".join(jieba.cut(text_string, cut_all=False))
            from sklearn.naive_bayes import MultinomialNB
            classifier = TextClassifier(clf_model=MultinomialNB(alpha=0.0001), space_path=self.space_path, test_space_path=self.tfidf_space_path,
                                        model_path=self.model_path)
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
    ret = app.use_classification(text_string='熟悉软件开发流程，熟练掌握SVN、Maven等开发和协同工具')
    print('return:', ret)
    ret = app.use_classification(text_string='维护数据库结构及数据的完整、一致性')
    print('return:', ret)
    ret = app.use_classification(text_string='精通至少一门以下语言：Go，Python，Java，C/C++，C# 根据项目需要，有兴趣并有能力学习go语言')
    print('return:', ret)



