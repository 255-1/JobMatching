import numpy as np
import pandas as pd
from .models import Jobinfo
import datetime


def get_data():
    '''
    设计人:刘若尘
    从数据库中读取数据，输出为DataFrame
    '''
    querySet = Jobinfo.objects.all().values()
    df = pd.DataFrame(list(querySet))
    return df


def count_Salary(df,option):
    '''
    设计人:潘雍昊,徐婧雯
    重构人:刘若尘
    计算各条职业的最低工资，平均工资，最高工资
    '''


    # df['salary']=df['salary'].apply(lambda x:x.strip('万/月'))
    #V1.0使用，以后可能会再用
    
    sp_data=df['salary'].apply(lambda x:x.split('-'))
    

    df['minSalary']=[float(d[0]) for d in sp_data.values]##min
    df['maxSalary']=[float(d[1]) for d in sp_data.values]##max
    df['avgSalary']=(df['minSalary']+df['maxSalary'])/2##average
    

    salary_companyType=df[[option,'minSalary', 'avgSalary','maxSalary']].groupby([option]).mean()

    return salary_companyType




def groupByOneFeature(df, options):
    '''
    设计人:周智骏
    根据option属性来分组df，返回组内数量前十的标签和数量
    @:param: option 单个属性，如salary
    :return: option属性下的分组内容，对应的数量
    '''
    groupByOptions = df.groupby(by=options);
    mydict = {}
    for i, j in groupByOptions:
        mydict[i] = len(j)
    sortUnify = sorted(mydict.items(), key=lambda entry: entry[1]
                       , reverse=True)[:10]
    labels = []
    values = []
    for t in sortUnify:
        labels.append(t[0])
        values.append(t[1])
    return labels, values

def groupByTwoFeatures(df, options):
    '''
    设计人:周智骏
    根据option属性列表进行组合，返回组合后数量前五的标签和数量
    :param options: 属性列表，如['exp','salary']
    :return: 分组组合的内容，对应的数量
    '''
    groupByTwoFeatures = df.groupby(by=options)
    mydict = {}
    for i, j, in groupByTwoFeatures:
        mydict[i] = len(j)
    salarySorted = sorted(mydict.items(), key=lambda entry: entry[1]
                          , reverse=True)[:5]
    tuples = []
    values = []
    for t in salarySorted:
        tuples.append(t[0])
        values.append(t[1])
    labels = []
    for t in tuples:
        labels.append(t[0] + "&" + t[1])
    return labels, values

# def offersInWeek(df):
#     '''
#     设计人:周智骏
#     一周内发布招聘信息发送数量
#     :param df:
#     :return: 日期列表，日期对应的招聘总数
#     '''
#     # key:月/日,value发布招聘的数量
#     dateInfo = {}
#     groupByDate = df.groupby(by="date")
#     for i, j in groupByDate:
#         date = i.split("-")
#         dateInfo[date[1] + "-" + date[2]] = len(j)
#     #对天进行排序
#     dateInfo = sorted(dateInfo.items(), key=lambda entry: int(entry[0].split('-')[1]))
#     #保留7天内的
#     dateInfo = dateInfo[-7::]
#     labels = []
#     values = []
#     for tp in dateInfo:
#         labels.append(tp[0])
#         values.append(tp[1])
#
#     return labels,values

def offersInWeek(df):
    '''
    设计人:周智骏
    一周内发布招聘信息发送数量
    :param df:
    :return: 日期列表，日期对应的招聘总数
    '''
    # key:月/日,value发布招聘的数量
    dateInfo = {}
    groupByDate = df.groupby(by="date")
    for i, j in groupByDate:
        date = i.split("-")
        # dateInfo[date[1] + "-" + date[2]] = len(j) ##数据库内没有更新时使用此行，仅用作测试
        current_month = datetime.datetime.now().month
        if int(date[1]) == current_month:
            dateInfo[date[1] + "-" + date[2]] = len(j)
    #对天进行排序
    dateInfo = sorted(dateInfo.items(), key=lambda entry: int(entry[0].split('-')[1]))
    #保留7天内的
    dateInfo = dateInfo[-7::]
    labels = []
    values = []
    for tp in dateInfo:
        labels.append(tp[0])
        values.append(tp[1])

    return labels,values

def companyOrientationDistribution(df):
    '''
    设计人:周智骏
    公司发展方向分布,饼状图，无需排大小
    :return:公司发展方向，对应的公司数量
    '''
    #修改原来的公司发展方向为第一个逗号前的内容
    df['companyOrientation'] = df['companyOrientation'].apply(lambda x: x.split(",")[0])
    labels = []
    values = []
    groupByCo = df.groupby(by='companyOrientation')
    for i, j in groupByCo:
        labels.append(i)
        values.append(len(j))
    return labels, values

def addressDistribution(df):
    '''
    设计人:周智骏
    公司地区分布，保留address“-”前的地区，
    并且保留发布数量前10的地区，饼状图
    :return:地区名称，招聘数量
    '''
    df['address'] = df['address'].apply(lambda x: x.split("-")[0])
    return groupByOneFeature(df, 'address')