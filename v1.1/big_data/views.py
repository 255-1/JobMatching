from django.shortcuts import render
from .models import Jobinfo
from django.http import HttpResponse
# Create your views here.
import random
from big_data.analyse import count_Salary,get_data
import numpy as np
import json
from big_data.static import *
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt


data=get_data() ##返回所有数据库内记录，数据结构为df
jobinfo=Jobinfo.objects.all() ##返回所有数据库内记录
jobinfo_n=int(len(data)/10000) ##所有数据库内记录的数量，单位为万



def home(request):
    '''
    家页面，返回'offer查询'的li项，使其自动生成
    （**'薪水统计'的li应该也按这个逻辑生成，不过因为li项较少，所以我就直接HARD CODE在html里面了，也许之后会改？**）
    '''
    
    uN=jobinfo.values('unifyName').distinct() ##数据库内unifyName字段的独特值,数据结构为字典,字典中每一个元素的键都为‘unifyName’

    uName=[ u.get('unifyName') for u in uN] ##取出每一个字典元素的值
    special='C#' ##需要用特殊字符表示的职业，不然无法被url正确解析
   
    return render(request,'big_data/home.html',{'uName':uName,'special':special,'jobinfo_n':jobinfo_n})


def get_jobinfo(request,unifyName,page):
    '''
    查询前端表单发来的职业信息
    '''
    
    ##根据前端传来的页值，得到数据的起始索引和接受索引
    per_page=20
    start_data=(page-1)*per_page
    end_data=page*per_page

    jobs=Jobinfo.objects.filter(unifyName=unifyName)##根据url中的值过滤从数据库中抽出的记录
    offers=jobs.count()##记录条数
    jobinfo=jobs[start_data:end_data]##制作记录切片
    
    # per_page=10##定下一页的记录数
    total_pages,extra_page=divmod(offers,per_page)##返回总共的页数和不满足一整页的记录数
    if extra_page:##如果有多余页,总页数加1
        total_pages += 1
    total_pages=range(1,total_pages+1)

    return render(request,'big_data/jobinfo.html',{"jobinfo":jobinfo,"offers":offers,"unifyName":unifyName,"total_pages":total_pages,"page":page,"per_page":per_page,'jobinfo_n':jobinfo_n})

      
def get_group_statistics(request,unifyName):
    '''
    获得不同公司种类职业的最低，平均，最高工资
    '''
    df=count_Salary(data,unifyName)##调用模块
    result=df.values
   
    companys=list(df.index)
    
    ls=[[unifyName, 'min', 'avg', 'max']]
    results=list(zip(companys,result))
    
    ##扁平化列表中的内容，使得二维列表中的每个元素是一个一维列表
    for i in range(len(results)):##！！有待优化！！##
        t=list()
        t.append(results[i][0])
        for j in results[i][1]:
            t.append(j)
        results[i]=t
       
    ##合并列表传递给前端
    ls.extend(results)

    if( len(ls) >7 ):  ##最多只取7组统计值
        ls=ls[:7]

    return render(request,'big_data/stats.html',{"data":ls,"option":unifyName,'jobinfo_n':jobinfo_n})


def detail(request):

    op="edu" ##设定groupByOneFeatrue的默认值
    lab,val=groupByOneFeature(data,op)

    op2=["jobName","edu"] ##设定groupByTwoFeatures的默认值
    lab2,val2=groupByTwoFeatures(data,op2)

    ofs_w=offersInWeek(data) 

    cmp_o=companyOrientationDistribution(data)

    dic={"lab":lab,"val":val,"lab2":lab2,"val2":val2,"ofs_w":ofs_w,"cmp_o":cmp_o,'jobinfo_n':jobinfo_n}

    return render(request,'big_data/detail.html',dic)


def get_groupByOneFeature(request): ## 响应ajax的请求，返回用户选择后筛选后的值
    
    option=request.GET.get("option",0)
    result=groupByOneFeature(data,option)
    
    return JsonResponse({"result":result,'jobinfo_n':jobinfo_n})


@csrf_exempt ##跳过跨域检查
def get_groupByTwoFeatures(request): ## 响应ajax的请求，返回用户选择后筛选后的值

    option=request.POST.getlist("option",[])
    result=groupByTwoFeatures(data,option)

    return JsonResponse({"result":result,'jobinfo_n':jobinfo_n})



    
