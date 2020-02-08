# 对职位名进行的分类

## 一. 目的
&ensp; &ensp; 利用NLP处理JobInfo文本，实现输入职业信息匹配相应职业。

## 二. 文本分类流程
    1. Excel文本提取（或Mysql数据数据）
    2. 预处理
    3. 中文分词
    4. 创建IF-IDF词向量空间
    5. 分类器
    6. 应用

## 三. 程序运行素材
+ 原始数据文件夹路径: ./data/ori_jobInfo/***.xlsx
+ 停用词表的路径: ./stop_words.txt

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
        + 参数说明：  
            ori_jobInfo_dir: str Excel数据表的父目录 eg: ./data/ori_jobInfo  
            corpus_dir: str 语料库路径 eg: ./data/jobInfo.corpus
    3. 操作流程：
        + 创建语料库目录：./data/jobInfo.corpus
        + 按Excel文件名在语料库下创建目录，如: ./data/jobInfo.corpus/前端
        + 提取Excel中的语句，保存到相应语料库目录中的txt文件中。  
            例如'前端.xlsx'中第一句文本语句则保存为：./data/jobInfo.corpus/前端/0.txt
        + 操作完成。

2. **预处理**
    1. 语料库简介：
        + jobInfo语句按行保存到相应职位名的目录中。  
            如 '前端.xlsx' 中有10个句子，则保存为 ./data/jobInfo.corpus/前端/0.txt～9.txt
        + 将上述语料库中的txt文件按比例划分为训练集、测试集。
    2. 代码模块：
        + 模块名：class DataCut(object)
        + 调用示例：
            ```python
            Data = DataCut(corpus_dir='./data/jobInfo.corpus', save_path='./data/jobInfo', ratio=0.3)
            Data.start_data_cut()
            print('训练集测试集划分完成')
            ```
        + 参数说明：  
            corpus_dir : str 语料库文件夹路径 ./data/jobInfo.corpus  
            save_path: str 训练集和测试集的父目录路径 ./data/jobInfo  
            ratio: float 训练集/训练集的比例  
    3. 操作流程：
        + 读取语料库下的文件，将文件按比例分别拷贝到相应的训练集和测试集目录下：./data/jobInfo/train 和 ./data/jobInfo/test
        + 例如：  
            语料库目录下 ./data/jobInfo.corpus/前端/0.txt～2.txt 三个txt文件分别拷贝到./data/jobInfo/train/前端/0.txt～1.txt和./data/jobInfo/test/前端/2.txt
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
        + 参数说明：  
            corpus_dir : str 语料库文件夹路径  eg: ./data/jobInfo.corpus/train 或/test    
            seg_path: str 分词后保存路径 eg: ./data/jobInfo.corpus/train_seg 或/test_seg  
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
        tdm: 存放计算后的权重矩阵
        vocabulary: 字典索引（词向量空间坐标），类似vocabulary={'Java':0, '计算机':1, '本科':3},对应tdm矩阵的列
        '''
        ```
    4. 将**测试集**文本转换成一个TF-IDF词向量空间，保存到testspace.dat
        训练集词向量空间：tfdifspace.dat  
        测试集词向量空间：testspace.dat   
        测试集词向量空间和训练集的vocabulary都相同，只是测试集词向量空间有自己的tdm
    5. 调用示例：
        1. 生成训练集和测试集词袋
            ```python
            Tfidf_Feature().corpus2Bunch(wordbag_path='./train_seg.dat', seg_path='./train_seg')
            
            ```
            wordbag_path: str 词袋保存路径  
            seg_path: str 分词后的语料库目录路径
        2. 向量化语料库
            ```python
            Tfidf_Feature().vector_space(bunch_mian_path='./train_seg.dat', bunch_secondary_path=None, stopword_path='./stop_words.txt', space_save_path='tfdifspace.dat')
            ```
            bunch_mian_path: str 训练集或测试集的词袋   
            bunch_secondary_path: str/None 训练集的语料库词袋路径，生成测试集特征向量的需要训练集的语料库词典  
            stopword_path: str 停用词文件路径  
            space_save_path: str 特征向量保存路径  
    5. 停用词列表：  
        为了节省空间，将训练集中的垃圾词汇去掉，包括语气词、标点符号等。  
        文本路径为：./stop_words/stop_word.txt

5. **分类器**
    1. 分类器：
        朴素贝叶斯、随机森林、逻辑回归和SVM四种分类器
    2. 模型保存路径分别为：
        ./data/jobInfo_app/models/bayes_NBclassifier.pkl  
        ./data/jobInfo_app/models/forest_NBclassifier.pkl  
        ./data/jobInfo_app/models/logistic_NBclassifier.pkl
        ./data/jobInfo_app/models/SVM_NBclassifier.pkl  
    3. 训练分类器：
        输入1: 训练集词向量（tfdifspace.dat）.tdm 特征向量
        输入2: 训练集词向量（tfdifspace.dat）.label 分类标签
        示例：
        ```python
        clf = MultinomialNB(alpha=0.001).fit(train_set.tdm, train_set.label)
        ```
    4. 目前分类器精度： 
        ```python
        ----------- 贝叶斯算法训练模型 ----------- 
        loading exists model...
        starting validation...
        精度:0.834
        召回:0.822
        f1-score:0.823
        ----------- 随机森林算法算法训练模型 ----------- 
        loading exists model...
        starting validation...
        精度:0.958
        召回:0.958
        f1-score:0.958
        ----------- Logistic回归模型 ----------- 
        loading exists model...
        starting validation...
        精度:0.959
        召回:0.959
        f1-score:0.959
        ----------- SVM模型 ----------- 
        loading exists model...
        starting validation...
        精度:0.758
        召回:0.744
        f1-score:0.744
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
        + 模型：
            + ./data/jobInfo_app/models/bayes_NBclassifier.pkl  
            + ./data/jobInfo_app/models/forest_NBclassifier.pkl  
            + ./data/jobInfo_app/models/logistic_NBclassifier.pkl
            + ./data/jobInfo_app/models/SVM_NBclassifier.pkl  
    3. 调用示例：
        ```python
        app = Application()
        print('--------------- 模型应用 --------------')
        ret = app.use_classification(text_string='熟悉软件开发流程，熟练掌握SVN、Maven等开发和协同工具', model='logistic')
        print('return:', ret)
        ```
        text_string: str/None 对一个字符串文本预测分类
        text_dir: str/None 对文本文档内容分类
        model: str/None 使用模型选择 model = 'bayes'/'forset'/'logistic'/'SVM'
        :return : str/None 返回预测类别结果
    4. 需要其他的源码路径：源码/*  
        包括其他预处理、中文分词 到 训练模型等全部流程在文件：done.py  
        + 运行支持文件：  
            + 停用词：./stop_words/stop_word.txt
            + Excel文件目录：./data/ori_jobInfo/**.xlsx
        
