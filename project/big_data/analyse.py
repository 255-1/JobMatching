

import pandas as pd
from django.shortcuts import render
from .models import Jobinfo
from django.http import HttpResponse



def get_data():
    '''
    从数据库中读取数据，输出为DataFrame
    '''
    querySet = Jobinfo.objects.all().values()
    df = pd.DataFrame(list(querySet))
    # df = pd.read_csv('D:/project/big_data/jobinfo.csv')#我这里数据库有点问题 用xls导的数据，李翼你自己重写一下这个函数保证输出是个DataFrame就好
    return df


def count_Salary(df,option):
    '''
    计算各条职业的最低工资，平均工资，最高工资
    '''


    df['salary']=df['salary'].apply(lambda x:x.strip('万/月'))
    
    sp_data=df['salary'].apply(lambda x:x.split('-'))
    

    df['minSalary']=[float(d[0]) for d in sp_data.values]
    df['maxSalary']=[float(d[1]) for d in sp_data.values]
    df['avgSalary']=(df['minSalary']+df['maxSalary'])/2
    

    salary_companyType=df[[option,'minSalary', 'avgSalary','maxSalary']].groupby([option]).mean()

    return salary_companyType



