from big_data.static import *

def getMapData():
    '''
    中国地图展示工资分布
    '''
    #从数据库读取数据
    addressData = getAddressDataToDF()
    #提取数据
    return dataExtractFromDF(addressData)

def getMapDataByUnifyName(unifyName):
    '''
    中国地区展示职位的工资数量分布
    '''
    #从数据库读取数据
    addressData = getAddressDataToDF(unifyName)
    #提取数据
    return dataExtractFromDF(addressData)

def getAddressDataToDF(unifyName="all"):
    '''
    读取最新10000条的地址数据
    :return:
    '''
    if "all" in unifyName:
        querySet = Jobinfo.objects.order_by("-id").values("address")[0:10000]
    else:
        querySet = Jobinfo.objects.filter(unifyName=unifyName).order_by("-id").values("address")[0:10000]
    return pd.DataFrame(list(querySet))

def dataExtractFromDF(df):
    '''
    从dataFrame中提取地区信息
    :param df:
    :return:
    '''
    #统计“市”为单位的数量
    df["address"] = df["address"].apply(lambda x: x.split("-")[0])
    groupByAddress = df.groupby(by="address")
    #计算数量
    areaList = []
    countList = []
    for i, j in groupByAddress:
        areaList.append(i)
        countList.append(len(j))
    return areaList,countList