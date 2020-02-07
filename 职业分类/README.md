# 对职位名进行的分类

## 一. 目的
&ensp; &ensp; 利用NLP处理JobInfo文本，实现输入职业信息匹配相应职业。

## 二. 文本分类流程
    1. Excel文本提取（或Mysql数据数据）
    2. 预处理
    3. 中文分词
    4. 权重策略
    5. 分类器和评价
    6. 应用

## 三. 程序运行素材
+ 原始数据文件夹路径: ./data/ori_jobInfo
+ 停用词表的路径: ./stop_words1.txt

## 四. 运行细节
1. **Excel文本提取（或Mysql数据数据）**
    1. Excel数据简介：
        + 原始素材Excal表格地址： ./data/ori_jobInfo目录下；
        + 在上述目录下为以职位名（eg: 前端、架构、JAVA 等）为文件名的Excel表格文件（eg: 前端.xlsx）；
        + 表格数据第一属性为jobInfo按行存储的数据信息（后3个属性为专业能力、个人能力和工具使用，暂未使用不做解释）；
    2. 代码模块：
        + 模块名：class ExtractData(object)
        + 调用示例：
            ```python
            OriData = ExtractData(ori_jobInfo_dir='./data/ori_jobInfo', corpus_dir='./data/jobInfo.corpus')
            OriData.start_extract()
            print('Excel语句提取完成')
            ```
    3. 操作流程：
        + 创建语料库目录：./data/jobInfo.corpus
        + 按Excel文件名在语料库下创建目录，如: ./data/jobInfo.corpus/前端
        + 提取Excel中的语句，保存到相应语料库目录中的txt文件中。例如'前端.xlsx'中第一句文本语句则保存为：./data/jobInfo.corpus/前端/0.txt
        + 操作完成。

2. **预处理**
    1. 语料库简介：
        + jobInfo语句按行保存到相应职位名的目录中，如 '前端.xlsx' 中有10个句子，则保存为 ./data/jobInfo.corpus/前端/0.txt～9.txt
        + 将上述语料库中的txt文件按比例划分为训练集、测试集。
    2. 代码模块：
        + 模块名：class DataCut(object)
        + 调用示例：
            ```python
            Data = DataCut(corpus_dir='./data/jobInfo.corpus', save_path='./data/jobInfo', ratio=0.3)
            Data.start_data_cut()
            print('训练集测试集划分完成')
            ```
        + 注意：参数ratio为训练集和测试集的比例。
    3. 操作流程：
        + 读取语料库下的文件，将文件按比例分别拷贝到相应的训练集和测试集目录下：./data/jobInfo/train 和 ./data/jobInfo/test
        + 例如：语料库目录下 ./data/jobInfo.corpus/前端/0.txt～2.txt 三个txt文件分别拷贝到./data/jobInfo/train/前端/0.txt～1.txt和./data/jobInfo/test/前端/2.txt
        + 操作完成。

3. **中文分词**
    1. 目的：
        对训练集的和测试集目录下的txt文件内容中文分词；
    2. 代码模块：
        + 模块名：class JobInfoSeg(object)
        + 调用示例：
            ```python
            JobInfoSeg().corpus_seg(curpus_path='./data/jobInfo/test', seg_path='./data/jobInfo/test_seg')
            print('分词完成')
            ```
        + 注意：参数ratio为训练集和测试集的比例。
    3. 操作流程：
        + 读取训练集和测试集目录下的txt文件内容，利用jieba分词，然后保存到./data/jobInfo/train_seg和test_seg目录下。
        + 操作完成。

4. **创建IF-IDF词向量空间**
    1. 以下词向量文件将保存在目录下： ./data/jobInfo_app/fearture_space
    2. 采用Scikit-Learn库中的**Bunch数据结构**来表示**训练集**和**测试集**两个数据集。
        将数据集（训练集和测试集）的信息进行Bunch化操作，然后分别储存为train_set.dat和test_set.dat，将训练集和测试集的数据。
        ```python
        from sklearn.datasets.base import Bunch
        bunch = Bunch(target_name=[], label=[], filenames=[], contents=[])
        '''
        target_name: 表示职位名类别的集合列表；
        label: txt文本的分别标签，即文本的夫目录名，如 软件开发；
        filenames: txt文本文件名（路径），如 ./data/jobInfo/test_seg/软件开发/28.txt；
        contents: 文本内的语句 
        '''
        ```
    3. 将**训练集**文本转换成一个TF-IDF词向量空间，保存到tfdifspace.dat
        ```python
        from sklearn.datasets.base import Bunch
        tfidfspace = Bunch(target_name=[], label=[], filenames=[], tdm=[], vocabulary={})
        '''
        target_name: 表示职位名类别的集合列表；
        label: txt文本的分别标签，即文本的夫目录名，如 软件开发；
        filenames: txt文本文件名（路径），如 ./data/jobInfo/test_seg/软件开发/28.txt；
        contents: 文本内的语句 
        rdm: 存放计算后的权重矩阵
        vocabulary: 字典索引（词向量空间坐标），类似vocabulary={'Java':0, '计算机':1, '本科':3},对应tdm矩阵的列
        '''
        ```
    4. 将**测试集**文本转换成一个TF-IDF词向量空间，保存到testspace.dat
        训练集词向量空间：tfdifspace.dat
        测试集词向量空间：testspace.dat
        测试集词向量空间和训练集的vocabulary都相同，只是测试集词向量空间有自己的tdm
    5. 停用词列表：
        为了节省空间，将训练集中的垃圾词汇去掉，包括语气词、标点符号等。文本路径为：./stop_words/stop_word.txt

5. **分类器**
    1. 分类器：
        朴素贝叶斯分类器
    2. 模型保存路径： ./data/jobInfo_app/models/NBclassifier.pkl
    3. 训练分类器：  
        输入1: 训练集词向量（tfdifspace.dat）.tdm 特征向量  
        输入2: 训练集词向量（tfdifspace.dat）.label 分类标签  
        示例：
        ```python
        clf = MultinomialNB(alpha=0.001).fit(train_set.tdm, train_set.label)
        ```
    4. 目前分类器精度： 
        ```python
        精度:0.833
        召回:0.821
        f1-score:0.822
        ```

6. **应用**
    对输入文本进行预测
    调用示例：
    1. 代码文件名：职业分类NLP应用/ClassifierJobNameApp.py
    2. 支持文件：
        + 停用词：./stop_words/stop_word.txt
        + 词向量文件：
            + ./data/jobInfo_app/fearture_space/train_set.dat
            + ./data/jobInfo_app/fearture_space/test_set.dat
            + ./data/jobInfo_app/fearture_space/tfdifspace.dat
            + ./data/jobInfo_app/fearture_space/testspace.dat
        + 模型：./data/jobInfo_app/models/NBclassifier.pkl
    3. 调用示例：
        you look , you know(看到源码应该就知道了)
        ```python
        app = Application()
        print('--------------- 模型应用 --------------')
        ret = app.use_classification(text_string='熟悉软件开发流程，熟练掌握SVN、Maven等开发和协同工具')
        print('return:', ret)
        ```
    4. 需要其他的源码路径：源码/*   
        包括其他预处理、中文分词 到 训练模型等全部流程在文件：done.py
        + 运行支持文件：
            + 停用词：./stop_words/stop_word.txt
            + Excel文件目录：./data/ori_jobInfo/**.xlsx
        
