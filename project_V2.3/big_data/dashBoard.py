from big_data.static import *
from copy import  deepcopy

#获取今天日期
today = datetime.datetime.now()
month = str(today.month)
day = str(today.day)##调试方便，注释
# day="2"
# month="3"
#获取7天前日期
lastWeek = today+datetime.timedelta(days=-7)
lastMonth = str(lastWeek.month)
lastDay = str(lastWeek.day)

#调试方便指定上一周的数据
# lastMonth = "2"
# lastDay = "2"

def transDateToMonthDay(df):
    '''
    将dataframe的date属性转为非格式化的月-日形式
    eg：2020-03-06->3-6
    :param df: 带有date属性的dataframe
    :return: 转变后的dataframe
    '''
    df['date'] = df['date'].apply(lambda x: str(int(x.split("-")[1])) + "-" + str(int(x.split("-")[2])))
    return df

def transDateToMonth(df):
    '''
    将dataframe的date属性转为非格式化的月形式
    eg：2020-03-06->3
    :param df: 带有date属性的dataframe
    :return: 转变后的dataframe
    '''
    df['date'] = df['date'].apply(lambda x: int(x.split("-")[1]))
    return df

def getDashBoardDataToDF(jobinfo):
    '''
    专门给仪表盘的数据，需要unifyName和salary，并且将salary转为平均工资
    :return: dataframe["salary(平均值后的)","unifyName"]
    '''
    querySet = jobinfo.values("salary", "unifyName","date")
    df = pd.DataFrame(list(querySet))
    # 工资列调整为每个offer的平均工资
    df['salary'] = df['salary'].apply(lambda x: np.mean(list(map(float, x.split("-")))))
    return df

#1
def topSalary(df):
    '''
    平均工资前5的职位
    :return:top5UnifyNameList 平均前五职位列表
            top5AverageList 前五职位对应的平均工资,保留小数点后2位，单位万/月
            top5OfferNumberList 前五对应的offer数量
    '''
    df = deepcopy(df)
    #获取所有的职位名以及对应的offer数量和平均工资
    unifyNameDict = {}
    groupByUnifyName = df.groupby(by="unifyName")
    for unifyName, groupDF in groupByUnifyName:
        unifyNameDict[unifyName] = []
        avgSalary = np.mean(groupDF["salary"])
        unifyNameDict[unifyName].append(round(avgSalary,2))
        unifyNameDict[unifyName].append(len(groupDF))
    #排序得到top5工资
    top5TupleList = sorted(unifyNameDict.items(),key=lambda entry:entry[1][0],reverse=True)[0:5]
    #提取成列表
    top5UnifyNameList=[]
    top5AvgSalaryList=[]
    top5OfferNumberList=[]
    for t in top5TupleList:
        top5UnifyNameList.append(t[0])
        top5AvgSalaryList.append(t[1][0])
        top5OfferNumberList.append(t[1][1])
    print(top5UnifyNameList)
    print(top5AvgSalaryList)
    print(top5OfferNumberList)
    return top5UnifyNameList,top5AvgSalaryList,top5OfferNumberList

#2
def offerNumberIncreaseTop5InWeek(df_range_0_30,df_range_30_60):
    '''
    一周offer增量前5
    :param : df_range_0_30 DataFrame 这个周期的jobinfo数据
             df_range_30_60 DataFrame 上个周期的jobinfo数据
    :return: unifyNameList 职位列表
            increaseNumberList 职位对应增量的offer数
            offerNumberList 职位对应的这个周期的offer数
    '''
    df_range_0_30 = deepcopy(df_range_0_30)
    df_range_30_60 = deepcopy(df_range_30_60)

    #按照职位名分组
    todayGroupByUnifyName = df_range_0_30.groupby(by="unifyName")
    lastGroupByUnifyName = df_range_30_60.groupby(by="unifyName")
    #提取数据

    unifyNameDict={} #unifyName:[increaseNumber, offerNumber]
    #后缀1为今天，后缀2为一周前的
    for unifyName1, groupDF1 in todayGroupByUnifyName:
        for unifyName2, groupDF2 in lastGroupByUnifyName:
            if(unifyName1==unifyName2):
                unifyNameDict[unifyName1] = []
                unifyNameDict[unifyName1].append(len(groupDF1)-len(groupDF2))
                unifyNameDict[unifyName1].append(len(groupDF1))
    # 排序得到top5增量
    top5TupleList = sorted(unifyNameDict.items(), key=lambda entry: entry[1][0], reverse=True)[0:5]
    #提取元组中的数据
    unifyNameList = []
    increaseNumberList = []
    offerNumberList = []
    for t in top5TupleList:
        unifyNameList.append(t[0])
        increaseNumberList.append(t[1][0])
        offerNumberList.append(t[1][1])
    print(unifyNameList)
    print(increaseNumberList)
    print(offerNumberList)
    return unifyNameList, increaseNumberList, offerNumberList


#3
def avgSalryInMonth(df):
    '''
    当月所有unifyName的平均薪水
    :return: unifyNameList职位列表
            avgSalaryList职位对应的平均工资,保留小数点后2位，单位万/月
    '''
    df = deepcopy(df)
    #筛选出当月的数据
    df = transDateToMonth(df)
    df = df.loc[df['date'] == int(month)]
    #获取职位列表和职位对应的平均工资
    unifyNameList = []
    avgSalaryList = []
    groupByUnifyName = df.groupby(by="unifyName")
    for unifyName, groupDF in groupByUnifyName:
        unifyNameList.append(unifyName)
        avgSalary = np.mean(groupDF["salary"])
        avgSalaryList.append(round(avgSalary,2))
    print(unifyNameList)
    print(avgSalaryList)
    return unifyNameList,avgSalaryList

#4
def avgSalaryEveryMonth(df):
    '''
    返回每个月全部offer的平均薪资
    :return:dateList 月份列表
            avgSalaryList 月份列表对应的平均薪资
    '''
    df = deepcopy(df)
    #调整date为月
    df = transDateToMonth(df)
    #获取数据
    dateList = []
    avgSalaryList = []
    groupByDate = df.groupby(by="date")
    for date, groupDF in groupByDate:
        dateList.append(date)
        avgSalary = np.mean(groupDF['salary'])
        avgSalaryList.append(round(avgSalary, 2))
    print(dateList)
    print(avgSalaryList)
    return dateList, avgSalaryList




#5
def offerNumberPercentChangeBetweenLastMonthAndThisMonth(df):
    '''
    今天和上个月同日的offer数量以及变化率
    :return:percentChange 变化率（%）
            lastMonthOfferNumber 上月同日的offer数量
            thisMonthOfferNumber 今天的offer数量
    '''
    df = deepcopy(df)
    #获取这个月和上个月日期
    lastMonth = str(int(month)-1 if month!=1 else 12)
    # 日期列调整为月-日类型
    df = transDateToMonthDay(df)
    # 筛选数据
    thisMonthOfferNumber = len(df.loc[df['date'] == month+"-"+day])
    lastMonthOfferNumber = len(df.loc[df['date'] == lastMonth+"-"+lastDay])

    percentChange=round(thisMonthOfferNumber/lastMonthOfferNumber,3)*100
    print(thisMonthOfferNumber)
    print(lastMonthOfferNumber)
    print(percentChange)
    return percentChange, lastMonthOfferNumber, thisMonthOfferNumber


#------补充------#
def get_msal_and_offer(df,args):
    '''
    查询给定unifyName的平均工资和offer数量
    :param df:DataFrame jobinfo的DataFrame
    :param args: list 需要查询的unifyName，list内元素应该为str
    :return: list unifyNameList 给定的unifyName
             list avgSalaryList 给定unifyName的平均工资
             list offerNumberList 给定unifyName的offer数
    '''
    df = deepcopy(df)

    unifyNameDict = {}
    groupByUnifyName = df.groupby(by="unifyName")
    for unifyName, groupDF in groupByUnifyName:
        if unifyName not in args:
            continue
        else:
            unifyNameDict[unifyName] = []
            avgSalary = np.mean(groupDF["salary"])
            unifyNameDict[unifyName].append(round(avgSalary, 2))
            unifyNameDict[unifyName].append(len(groupDF))

    top5TupleList = unifyNameDict.items()
    # 提取成列表
    unifyNameList = []
    avgSalaryList = []
    offerNumberList = []
    for t in top5TupleList:
        unifyNameList.append(t[0])
        avgSalaryList.append(t[1][0])
        offerNumberList.append(t[1][1])
    print(unifyNameList)
    print(avgSalaryList)
    print(offerNumberList)
    return unifyNameList, avgSalaryList, offerNumberList
