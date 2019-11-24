#!/usr/bin/env python
# -*- coding: UTF-8 -*-
'''=================================================
@Project -> File   ：python_projects -> test
@IDE    ：PyCharm
@Author ：Li & PYH
@Date   ：2019/11/24 9:38 PM
@Desc   ：
=================================================='''

import pymysql
import numpy as np
import os
import json
import pandas as pd
from collections import defaultdict
from pandas import Series,DataFrame
from matplotlib import pyplot as plt


class GetSearch(object):
    '''
    得到用户搜索内容，并根据内容分类。
    '''
    def __init__(self, search_content, job_info):
        '''
        @Auther： Li
        获取用户搜索文本和数据库信息
        :param search_content: 用户搜索文本（char类型）;
        :param job_info: 数据库信息（df类型）;
        '''
        self.search_content = search_content
        self.job_info = job_info

    def classifying(self):
        '''
        @Auther： Li
        根据用户输入的信息进行分类，调用相应的字段分析方法，并返回前端所需信息；
        !!!注意：调用的函数需有返回（return）。
        :return: 调用字段分析函数的返回信息(若用户输没有匹配返回'error'）;
        '''
        return {
            '薪酬图': count_Salary(self.job_info),
            'b': '输出XX图数据json',
            'c': '输出XX图数据json',
        }.get(self.search_content, 'error')



def count_Salary(df):
    '''
    @Auther： PYH
    计算各条职业的最低工资，平均工资，最高工资
    '''
    salary = list(df['salary'])
    salary_n = []
    avgSalary = []
    minSalary = []
    maxSalary = []
    for i in salary:
        salary_n.append(i[0:-3])#过滤万/月
    for i in salary_n:
        a = i.split('-',1)
        avgSalary.append(avg(a[0],a[1]))#获得平均值
        minSalary.append(float(a[0]))#获得最低值
        maxSalary.append(float(a[1]))#获得最高值
    df['minSalary'] = minSalary
    df['avgSalary'] = avgSalary
    df['maxSalary'] = maxSalary

    df = analysis_json('edu', df)
    return df

def analysis_dict(attribute,df):
    '''
    @Auther： PYH
    将df转换成dict输出
    '''
    try:
        salary = df[[attribute,'minSalary','avgSalary','maxSalary']].groupby([attribute]).mean()
        res = salary.to_dict(orient='index')
    except KeyError as e:
        print(e);
    return res

def analysis_json(attribute,df):
    '''
    @Auther： PYH
    将df转换成json输出
    '''
    try:
        salary = df[[attribute,'minSalary','avgSalary','maxSalary']].groupby([attribute]).mean()
        res = salary.to_json(orient='index')
    except KeyError as e:
        print(e);
    return res

def avg(a,b):
    '''
    @Auther： PYH
    算均值
    '''
    return (float(a)+float(b))/2

