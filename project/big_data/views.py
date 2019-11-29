from django.shortcuts import render
from .models import Jobinfo
from django.http import HttpResponse
# Create your views here.
import random
from big_data.analyse import count_Salary,get_data
import numpy as np
import json


def home(request):
    
    return render(request,'big_data/base.html')

def get_jobinfo(request,jobName,page):
    '''
    查询前端表单发来的职业信息
    '''
    
    ##根据前端传来的页值，得到数据的起始索引和接受索引
    start_data=(page-1)*10
    end_data=page*10

    jobs=Jobinfo.objects.filter(jobName=jobName)##根据url中的值过滤从数据库中抽出的记录
    offers=jobs.count()##记录条数
    jobinfo=jobs[start_data:end_data]##制作记录切片
    
    per_page=10##定下一页的记录数
    total_pages,extra_page=divmod(offers,per_page)##返回总共的页数和不满足一整页的记录数
    if extra_page:##如果有多余页,总页数加1
        total_pages += 1
    total_pages=range(1,total_pages+1)

    return render(request,'big_data/jobinfo.html',{"jobinfo":jobinfo,"offers":offers,"jobName":jobName,"total_pages":total_pages})

      
    
def get_group_statistics(request):
    '''
    获得不同公司种类职业的最低，平均，最高工资
    '''
    data=get_data()
    option = request.POST.get("option")##从前端接受的字段值

    df=count_Salary(data,option)##调用模块
    result=df.values
   

    companys=list(df.index)
    

    data=[['product', 'min', 'avg', 'max']]
    results=list(zip(companys,result))
    
    ##扁平化列表中的内容，使得二维列表中的每个元素是一个一维列表
    for i in range(len(results)):##！！有待优化！！##
        t=list()
        t.append(results[i][0])
        for j in results[i][1]:
            t.append(j)
        results[i]=t
    
    
    ##合并列表传递给前端
    data.extend(results)

    return render(request,'big_data/stats.html',{"data":data})