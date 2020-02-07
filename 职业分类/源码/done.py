# -*- coding: utf-8 -*-
# @Time    : 2020/1/29
# @Author  : li
# @FileName: done.py
# @Desc    :


import os
import sys
import xlrd
import random
import shutil
import jieba
import pickle
import glob
import time
from sklearn.externals import joblib
from sklearn import metrics
from threading import Thread, Lock
from queue import Queue
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
        :param filepath: 文本地址（博文中有小错，应为txt文件）
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

class DataCut(object):
    def __init__(self, corpus_dir, save_path, ratio):
        self.corpus_dir = corpus_dir
        self.save_path = save_path
        self.ratio = ratio
        self.THREADLOCK = Lock()

        self.end = []

        # 检测目录是否存在，不存在则创建
        func_tools().check_dir_exist(dir=self.corpus_dir)
        func_tools().check_dir_exist(dir=self.save_path)

    def start_data_cut(self):
        # 在./data/jobInfo/目录下建立测试集和训练集 --- 【./data/jobInfo/train】和【./data/jobInfo/test】
        train_folder = os.path.join(self.save_path, "train")
        test_folder = os.path.join(self.save_path, "test")
        print(train_folder)
        print(test_folder)

        q = Queue()

        for basefolder in os.listdir(self.corpus_dir):
            # 语料库文件详细地址：return './data/jobInfo.corpus' +  '/Java'
            full_path = os.path.join(self.corpus_dir, basefolder)

            train_path = os.path.join(train_folder, basefolder)
            func_tools().check_dir_exist(dir=train_path)
            test_path = os.path.join(test_folder, basefolder)
            func_tools().check_dir_exist(dir=test_path)
            full_path += "/*.txt"
            q.put((full_path, train_path, test_path, self.ratio))

        for i in range(8):
            Thread(target=self.copyfile, args=(q,)).start()

        # 判断是否完成
        while len(self.end) < len(os.listdir(self.corpus_dir)):
            time.sleep(2)

    def copyfile(self, q):
        '''
        拷贝数据到测试集和训练集目录下
        :param q:
        :return:
        '''
        while not q.empty():
            full_folder, train, test, divodd = q.get()
            files = glob.glob(full_folder)
            filenum = len(files)
            testnum = int(filenum * divodd)
            testls = random.sample(list(range(filenum)), testnum)
            for i in range(filenum):
                if i in testls:
                    shutil.copy(files[i], os.path.join(test, os.path.basename(files[i])))
                else:
                    shutil.copy(files[i], os.path.join(train, os.path.basename(files[i])))

            self.end.append('end')
            with self.THREADLOCK:
                print(full_folder)

class ExtractData(object):
    '''
    提取原始Excel数据表中的数据【./data/ori_jobInfo'】，做成完整的语料库数据集【./data/jobInfo.corpus】。
    准备进行数据测试集和训练集划分
    '''
    def __init__(self, ori_jobInfo_dir, corpus_dir):
        self.ori_jobInfo_dir = ori_jobInfo_dir
        self.corpus_dir = corpus_dir

    def start_extract(self):
        for basefolder in os.listdir(self.ori_jobInfo_dir):
            # Mac有.DS_Store文件，跳过
            if basefolder.startswith('.DS'):
                continue
            # 原始Excel数据集地址
            excelDir = os.path.join(self.ori_jobInfo_dir, basefolder)
            # print(excelDir)

            # 语料库数据集地址
            corpusDirName = os.path.join(self.corpus_dir, basefolder[:-5])
            # 检测目录是否存在，不存在则创建
            func_tools().check_dir_exist(dir=corpusDirName)

            # 将原始Excel数据内容拷贝到相应语料库数据集【./data/jobInfo.corpus】
            self.excel_to_corpus(excel_path=excelDir, corpus_path=corpusDirName)

    def excel_to_corpus(self, excel_path, corpus_path):
        readExcel = xlrd.open_workbook(excel_path)
        sheet = readExcel.sheet_by_name('Sheet1')
        nrows = sheet.nrows  # 返回行：100

        for row in range(nrows-1):
            # 测试都放在一个txt文本中
            txt_path = os.path.join(corpus_path, str(row)+'.txt')     # 0.txt ~ XX.txt

            with open(txt_path, "w") as file:
                file.write(sheet.cell(row+1, 0).value)

class JobInfoSeg(object):
    def __init__(self):
        # userdict = './userdict.txt'
        # jieba.load_userdict(userdict)
        self.LOCK = Lock()
        self.end = []


    def text_segment(self, q):
        """
        对一个类别目录下进行分词
        """
        while not q.empty():
            from_dir, to_dir = q.get()
            with self.LOCK:
                print(from_dir)
            files = os.listdir(from_dir)
            for name in files:
                if name.startswith('.DS_Store'):
                    continue
                from_file = os.path.join(from_dir, name)
                to_file = os.path.join(to_dir, name)

                content = func_tools().readfile(from_file)
                seg_content = jieba.cut(content)
                func_tools().savefile(to_file, ' '.join(seg_content))
            self.end.append('end')



    def corpus_seg(self, curpus_path, seg_path):
        """
        对文本库分词，保存分词后的文本库,目录下以文件归类 curpus_path/category/1.txt, 保存为 seg_path/category/1.txt
        """
        func_tools().check_dir_exist(seg_path)
        q = Queue()
        cat_folders = os.listdir(curpus_path)
        for folder in cat_folders:
            from_dir = os.path.join(curpus_path, folder)
            to_dir = os.path.join(seg_path, folder)
            func_tools().check_dir_exist(to_dir)

            q.put((from_dir, to_dir))

        for i in range(len(cat_folders)):
            Thread(target=self.text_segment, args=(q,)).start()
        # 判断是否完成
        while len(self.end) < len(cat_folders):
            time.sleep(2)

class Tfidf_Feature(object):
    def __init__(self):
        pass

    def corpus2Bunch(self, wordbag_path, seg_path):
        catelist = os.listdir(seg_path)  # 获取seg_path下的所有子目录，也就是分类信息

        # 创建一个Bunch实例
        bunch = Bunch(target_name=[], label=[], filenames=[], contents=[])
        bunch.target_name.extend(catelist)
        '''
        extend(addlist)是python list中的函数，意思是用新的list（addlist）去扩充
        原来的list
        '''
        # 获取每个目录下所有的文件
        for mydir in catelist:
            class_path = seg_path + '/' + mydir + '/'  # 拼出分类子目录的路径
            file_list = os.listdir(class_path)  # 获取class_path下的所有文件
            for file_path in file_list:  # 遍历类别目录下文件
                fullname = class_path + file_path  # 拼出文件名全路径
                bunch.label.append(mydir)
                bunch.filenames.append(fullname)
                bunch.contents.append(func_tools().readfile(fullname))  # 读取文件内容
                '''append(element)是python list中的函数，意思是向原来的list中添加element，注意与extend()函数的区别'''

        func_tools().writeobj(wordbag_path, bunch)
        print("构建文本对象结束: " + wordbag_path)

    def vector_space(self, bunch_mian_path, bunch_secondary_path, stopword_path, space_save_path):
        stpwrdlst = func_tools().readfile(stopword_path).split()  # 读取停用词
        bunch = func_tools().readobj(bunch_mian_path)  # 导入分词后的词向量bunch对象
        # 构建tf-idf词向量空间对象
        tfidfspace = Bunch(target_name=bunch.target_name, label=bunch.label, filenames=bunch.filenames, tdm=[],
                           vocabulary={})

        if bunch_secondary_path:
            '''test'''
            trainbunch = func_tools().readobj(bunch_secondary_path)
            tfidfspace.vocabulary = trainbunch.vocabulary
            vectorizer = TfidfVectorizer(stop_words=stpwrdlst, sublinear_tf=True, max_df=0.5,
                                         vocabulary=trainbunch.vocabulary)
            tfidfspace.tdm = vectorizer.fit_transform(bunch.contents)
        else:
            '''train'''
            # 此时tdm里面存储的就是if-idf权值矩阵
            vectorizer = TfidfVectorizer(stop_words=stpwrdlst, sublinear_tf=True, max_df=0.5)
            tfidfspace.tdm = vectorizer.fit_transform(bunch.contents)
            tfidfspace.vocabulary = vectorizer.vocabulary_

        func_tools().writeobj(space_save_path, tfidfspace)

        print("if-idf词向量空间实例创建成功: " + space_save_path)

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
        # 原始数据文件夹路径
        self.ori_jobInfo_dir = './data/ori_jobInfo'
        func_tools().check_dir_exist(dir=self.ori_jobInfo_dir)
        # 语料库文件夹路径
        self.jobInfo_corpus_dir = './data/jobInfo.corpus'
        func_tools().check_dir_exist(dir=self.jobInfo_corpus_dir)
        # 训练集和测试集
        self.jobInfo_dir = './data/jobInfo'
        func_tools().check_dir_exist(dir=self.jobInfo_dir)
        # 训练集和测试集
        self.jobInfo_app = './data/jobInfo_app'
        func_tools().check_dir_exist(dir=self.jobInfo_app)

        # 训练集和测试集比例
        self.ratio = 0.1
        # 训练集路径
        self.train_dir = os.path.join(self.jobInfo_dir, 'train')
        func_tools().check_dir_exist(dir=self.train_dir)
        # 测试集路径
        self.test_dir = os.path.join(self.jobInfo_dir, 'test')
        func_tools().check_dir_exist(dir=self.test_dir)
        # 分词后训练集
        self.train_seg_dir = os.path.join(self.jobInfo_dir, 'train_seg')
        func_tools().check_dir_exist(dir=self.train_seg_dir)
        # 分词后训练集
        self.test_seg_dir = os.path.join(self.jobInfo_dir, 'test_seg')
        func_tools().check_dir_exist(dir=self.test_seg_dir)
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

        # 训练分类器迭代次数,alpha越小，迭代次数越多，精度越高
        self.alpha_num = 0.00001

    def clean_train_test(self):
        print('--------------- 数据清洗 训练集&测试集 --------------')

        OriData = ExtractData(ori_jobInfo_dir=self.ori_jobInfo_dir, corpus_dir=self.jobInfo_corpus_dir)
        OriData.start_extract()
        print('Excel语句提取完成')

        Data = DataCut(corpus_dir=self.jobInfo_corpus_dir, save_path=self.jobInfo_dir, ratio=self.ratio)
        Data.start_data_cut()
        print('训练集测试集划分完成')

    def jieba_seg(self):
        print('--------------- jieba 分词 --------------')
        # 未分成的训练集和测试集
        ori_train_dir = os.path.join(self.jobInfo_dir, 'train')
        ori_test_dir = os.path.join(self.jobInfo_dir, 'test')
        # 分词后的测试集和训练集
        train_dir = os.path.join(self.jobInfo_dir, 'train_seg')
        test_dir = os.path.join(self.jobInfo_dir, 'test_seg')

        JobInfoSeg().corpus_seg(curpus_path=ori_train_dir, seg_path=train_dir)
        JobInfoSeg().corpus_seg(curpus_path=ori_test_dir, seg_path=test_dir)
        print('分词完成')

    def tfidf(self):
        print('--------------- 构建词袋 --------------')
        # 对训练集进行Bunch化操作：
        Tfidf_Feature().corpus2Bunch(self.train_wordbag_path, self.train_seg_dir)
        # 对测试集进行Bunch化操作：
        Tfidf_Feature().corpus2Bunch(self.test_wordbag_path, self.test_seg_dir)

        Tfidf_Feature().vector_space(bunch_mian_path=self.train_wordbag_path, bunch_secondary_path=None,
                                     stopword_path=self.stopword_path, space_save_path=self.space_path)
        Tfidf_Feature().vector_space(bunch_mian_path=self.test_wordbag_path, bunch_secondary_path=self.space_path,
                                     stopword_path=self.stopword_path, space_save_path=self.tfidf_space_path)

    def create_model(self):
        # 多项式贝叶斯算法训练模型
        print('--------------- 多项式贝叶斯算法训练模型 --------------')
        from sklearn.naive_bayes import MultinomialNB
        # 训练分类器：输入词袋向量和分类标签，alpha:0.001 alpha越小，迭代次数越多，精度越高
        clf = MultinomialNB(alpha=self.alpha_num)
        # 模型存放的文件夹
        func_tools().check_dir_exist(dir=self.model_dir)
        classifier = TextClassifier(clf_model=clf, space_path=self.space_path, test_space_path=self.tfidf_space_path, model_path=self.model_path)
        classifier.validation()

    def use_classification(self, text_dir=None, text_string=None):
        if text_string:
            seg_text_string = " ".join(jieba.cut(text_string, cut_all=False))
            from sklearn.naive_bayes import MultinomialNB
            classifier = TextClassifier(clf_model=MultinomialNB(alpha=self.alpha_num), space_path=self.space_path, test_space_path=self.tfidf_space_path,
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
    app.clean_train_test()
    app.jieba_seg()
    app.tfidf()
    app.create_model()

    print('--------------- 模型应用 --------------')
    ret = app.use_classification(text_string='熟悉软件开发流程，熟练掌握SVN、Maven等开发和协同工具')
    print('return:', ret)




