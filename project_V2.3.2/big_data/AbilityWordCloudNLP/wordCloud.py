import collections
import re
import pandas as pd
from .classification import JobinfoClassifier
from ..models import Jobinfo
import os

queryLimit = 50   #查询数量


path = __file__
cwd = '\\'.join(path.split('\\')[:-1])

print(os.path.join(cwd, 'SVM.model'))
modelPath = os.path.join(cwd, 'SVM.model')
stopWordsPath = os.path.join(cwd, 'stopwords.txt')
addWordsPath = os.path.join(cwd, 'addwords.txt')
wordsIdPath = os.path.join(cwd, 'words_id.txt')

wordCloudReRule = re.compile('\t|\n|\.|-|:|;|\)|\(|\?|"|。|、|，|,|；|/|[1234567890]')
wordCloudRemoveWordList = ['随着', '对于', '通常', '如果', '我们', '需要', '具有', '以上', '能力', '优先', '工作', '公司', '具备',
                      '良好', '熟悉', '相关', '负责', '完成', '基于', '以上学历', '微信 ', '分享', '职能', '类别','岗位职责','关键字',
                        '熟练掌握']


def getWordCloudData(unifyName):
    '''
    通过unifyName得到数据库最新前50个jobinfo的个人技能，专业技能，工具使用的词频列表
    :param unifyName: 职位统一名
    :return: label1WordList 专业技能词频高->低的词语列表
             label1CountList 专业技能词频高->低的频率列表
             label2WordList 个人技能词频高->低的词语列表
             label2CountList 个人技能词频高->低的频率列表
             label3WordList 工具使用词频高->低的词语列表
             label3CountList 工具使用词频高->低的频率列表
    '''
    # 数据库得到数据
    df = getDataToDF(unifyName)
    #数据库jobinfo的分句处理
    df = splitJobInfoFromDF(df)
    #获取预测结果
    classifier = JobinfoClassifier(modelPath,df,stopWordsPath,addWordsPath,wordsIdPath)
    result = classifier.predict()
    #通过DataFrame类型的预测结果构建{分类结果:分词}的字典{key分类标签:value分词内容}
    labelWordDict = predictResultDFToLabelWordDict(result)
    #词云前的预处理，将字典中的不符合元素清除
    validLabel1WordList,validLabel2WordList,validLabel3WordList=cleaningLabelWordDict(labelWordDict)
    #计算词频，得到词频字典{分词:出现数}
    label1WordCountDict = collections.Counter(validLabel1WordList)
    label2WordCountDict = collections.Counter(validLabel2WordList)
    label3WordCountDict = collections.Counter(validLabel3WordList)
    #提取字频词典
    label1WordList,label1CountList = getFrequencyFromCounter(label1WordCountDict)
    label2WordList,label2CountList = getFrequencyFromCounter(label2WordCountDict)
    label3WordList,label3CountList = getFrequencyFromCounter(label3WordCountDict)
    return label1WordList,label1CountList,label2WordList,label2CountList,label3WordList,label3CountList

def getDataToDF(unifyName):
    '''
    数据库得到数据,返回pd.DataFrame类型数据
    :param unifyName:
    :return:
    '''
    jobs = Jobinfo.objects.filter(unifyName=unifyName).order_by("-id")
    jobInfoQuerySet = jobs.values('jobInfo')[0:queryLimit]
    return pd.DataFrame(list(jobInfoQuerySet))

def splitJobInfoFromDF(df):
    '''
    把jobinfo中的段落按照'linefeed'规则分句，并整合成字符串
    '''
    df["jobInfo"] = df["jobInfo"].apply(lambda x: x.split("linefeed"))
    df["jobInfo"] = df["jobInfo"].apply(lambda x: ''.join(x))
    return df

def predictResultDFToLabelWordDict(result):
    '''
    将预测结果的pd.DataFrame类型转为{label:wordList}字典
    :param result: 模型预测结果
    :return: {label:wordList}字典
    '''
    labelWordDict = {}
    labelWordDict[1] = []
    labelWordDict[2] = []
    labelWordDict[3] = []
    for i in range(len(result["分割后的词"].values)):
        words = result["分割后的词"].values[i]
        label = result["分类"].values[i]
        labelWordDict[label] += words
    return labelWordDict

def cleaningLabelWordDict(labelWordDict):
    '''

    :param labelWordDict: {分类标签:分词列表}字典
    :return: 分类标签1的合法词语列表
            分类标签2的合法词语列表
            分类标签3的合法词语列表
    '''
    label1WordList = []
    label2WordList = []
    label3WordList = []
    for label, wordList in labelWordDict.items():
        for word in wordList:
            word = re.sub(wordCloudReRule, '', word)
            # 去除分词结果长度<2以及在停用词列表里面的词
            if len(word) > 2 and (word not in wordCloudRemoveWordList):
                if label == 1: label1WordList.append(word)
                if label == 2: label2WordList.append(word)
                if label == 3: label3WordList.append(word)
    return label1WordList,label2WordList,label3WordList

def getFrequencyFromCounter(wordCountDict):
    '''
    从词频字典{word:totalNumber}word统一为wordList,totalNumber统一成numberList
    :param wordCountDict: 词频字典
    :return:wordList和numberLists
    '''
    wordList = []
    countList = []
    for k,v in wordCountDict.items():
        wordList.append(k)
        countList.append(v)
    return wordList,countList

