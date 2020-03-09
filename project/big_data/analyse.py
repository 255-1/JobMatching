

import pandas as pd
from django.shortcuts import render
from .models import Jobinfo
from django.http import HttpResponse



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
    
    sp_data=df['salary'].apply(lambda x:x.split('-'))
    

    df['minSalary']=[float(d[0]) for d in sp_data.values]##min
    df['maxSalary']=[float(d[1]) for d in sp_data.values]##max
    df['avgSalary']=(df['minSalary']+df['maxSalary'])/2##average
    

    salary_companyType=df[[option,'minSalary', 'avgSalary','maxSalary']].groupby([option]).mean()

    return salary_companyType



