from django.shortcuts import render, redirect,HttpResponse
from .models import Jobinfo,User
from big_data.analyse import count_Salary,get_data
from big_data.static import *
from django.http import JsonResponse, HttpResponseRedirect
from django.views.decorators.csrf import csrf_exempt
from big_data.nlp import *
import jieba
import pandas as pd
import joblib
from .forms import  *
from big_data.map import getMapData
from .ClassifierJobNLP.ClassifierJobNameApp import Application
from .AbilityWordCloudNLP.wordCloud import getWordCloudData
import os
import re
from big_data.dashBoard import *



jobinfo=Jobinfo.objects.all()##返回所有数据库内记录
data=pd.DataFrame(list(jobinfo.values()))##返回所有数据库内记录，数据结构为df
jobinfo_n=int(len(data)/10000) ##所有数据库内记录的数量，单位为万
dash_df=getDashBoardDataToDF(jobinfo)##dashboard.py用df



def login_auth(func,*args,**kwargs):
    '''
   登陆检验装饰器
    '''
    def is_log(request,*args,**kwargs):
        if request.session.get('is_login', None):
            username = request.session['username']
            kwargs.update(username = username)
            response = func(request, **kwargs)
            return response
        else:
            return HttpResponse('请先登入')

    return is_log


def home(request):
    '''
    家页面，返回'offer查询'的li项，使其自动生成
    （**'薪水统计'的li应该也按这个逻辑生成，不过因为li项较少，所以我就直接HARD CODE在html里面了，也许之后会改？**）
    '''

    ##判断用户是否已经登陆
    if request.session.get('is_login', None):
        username=request.session['username']
    else:
        username=None

    # uN=jobinfo.values('unifyName').distinct() ##数据库内unifyName字段的独特值,数据结构为字典,字典中每一个元素的键都为‘unifyName’

    # uName=[ u.get('unifyName') for u in uN] ##取出每一个字典元素的值
    # special='C#' ##需要用特殊字符表示的职业，不然无法被url正确解析

    areaList, countList = getMapData()
    dic = {
           'jobinfo_n':jobinfo_n,
           'username':username,
           'areaList': areaList,
           'countList': countList}

    return render(request,'big_data/home.html',dic)

@login_auth
def get_jobinfo(request,**kwargs):
    '''
    查询前端表单发来的职业信息
    '''

    ##根据前端传来的页值，得到数据的起始索引和接受索引

    username = kwargs['username']
    unifyName = kwargs['unifyName']
    page = kwargs['page']



    uN = jobinfo.values('unifyName').distinct()  ##数据库内unifyName字段的独特值,数据结构为字典,字典中每一个元素的键都为‘unifyName’

    uName = [u.get('unifyName') for u in uN]  ##取出每一个字典元素的值
    special = 'C#'  ##需要用特殊字符表示的职业，不然无法被url正确解析

    per_page=20#定下一页的记录数
    start_data=(page-1)*per_page
    end_data=page*per_page

    jobs=Jobinfo.objects.filter(unifyName=unifyName,date="2020-01-09")##根据url中的值过滤从数据库中抽出的记录
    ##这里如果我没记错，应当取最新一天的数据，这里为了调试方便取一个定值

    offers=jobs.count()##记录条数
    job_dices=jobs[start_data:end_data]##制作记录切片

    total_pages,extra_page=divmod(offers,per_page)##返回总共的页数和不满足一整页的记录数
    if extra_page:##如果有多余页,总页数加1
        total_pages += 1
    total_pages=range(1,total_pages+1)

    dic={"job_dices":job_dices,
         "offers":offers,
         "unifyName":unifyName,
         "total_pages":total_pages,
         "page":page,
         "per_page":per_page,
         'jobinfo_n':jobinfo_n,
         'uName': uName,
         'special': special,
         'username':username}

    return render(request,'big_data/dashboard/jobinfo.html',dic)


@login_auth
def get_group_statistics(request,**kwargs):
    '''
    获得不同公司种类职业的最低，平均，最高工资
    '''
    username = kwargs['username']
    unifyName = kwargs['unifyName']

    option_dc = {'jobName': '职业',
                 'edu': '教育背景',
                 'exp': '工作经验',
                 'companyType': '公司类型',
                 'address': '地区',
                 'companyOrientation': '公司领域'}##选项转换字典

    df = count_Salary(data, unifyName)  ##调用模块

    result =[df.iloc[:7, i].values.tolist()for i in range(3)]
    lab = list(df.index)[:7]
    lab = [re.sub(r'[\(\[\)\]]','',i) for i in lab]##去掉一些不必要的符号

    # if unifyName == 'companyOrientation':
    #     lab = [i.split(',')[1] for i in lab]##当选项为公司领域时,取领域的简称。
    ##这地方跟数据有关,可能要取不同的分割符

    unifyName = option_dc[unifyName]##把选项转换位中文

    dic = {'lab':lab,
           "data": result,
           "option": unifyName,
           'jobinfo_n': jobinfo_n,
           'username': username}

    return render(request, 'big_data/dashboard/stats.html', dic )


@login_auth
def detail(request,**kwargs):
    '''
    单,双条件筛选下的offer前5统计;
    一周内的offer发布格式；offer在不同领域的分布
    '''
    username = kwargs['username']
    op="edu" ##设定groupByOneFeatrue的默认值
    lab,val=groupByOneFeature(data,op)

    op2=["jobName","edu"] ##设定groupByTwoFeatures的默认值
    lab2,val2=groupByTwoFeatures(data,op2)

    ofs_w=offersInWeek(data)

    cmp_o=companyOrientationDistribution(data)

    dic={"lab":lab,
         "val":val,
         "lab2":lab2,
         "val2":val2,
         "ofs_w":ofs_w,
         "cmp_o":cmp_o,
         'jobinfo_n':jobinfo_n,
         'username':username}

    return render(request,'big_data/dashboard/detail.html',dic)


def get_groupByOneFeature(request): ## 响应ajax的请求，返回用户选择后筛选后的值

    option=request.GET.get("option",0)
    result=groupByOneFeature(data,option)

    return JsonResponse({"result":result,'jobinfo_n':jobinfo_n})


@csrf_exempt ##跳过跨域检查
def get_groupByTwoFeatures(request): ## 响应ajax的请求，返回用户选择后筛选后的值

    option=request.POST.getlist("option",[])
    result=groupByTwoFeatures(data,option)

    return JsonResponse({"result":result,'jobinfo_n':jobinfo_n})

def page_not_found(request):##处理未知域名
    return render(request,'big_data/404.html')



#------------登陆注册+修改个人信息----------------------#


@csrf_exempt
def login(request):
    if request.session.get('is_login',None):
        return redirect('/')

    if request.method == 'GET':
        return render(request,'big_data/login/login.html')

    if request.method == 'POST':
        message = '登入成功！'
        form = UserForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            try:
                user = User.objects.get(username=username)
                print(user)
                if user.password != password:
                    message = '密码错误'
                else:
                    request.session['is_login'] = True
                    request.session['username'] = username
            except:
                message = '账号错误'
        else:
            print(form.errors)
            message = form.errors

    return JsonResponse({"message":message})


def logout(request):
    if request.session.get('is_login',None):
        request.session.flush()
        return redirect('/')
    else:
        return redirect('/')


@csrf_exempt
def register(request):

    if request.method == 'GET':
        return render(request, 'big_data/login/register.html')
    if request.method == 'POST':
        message = '注册成功！'
        form = RegisterForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            try:
                user = User.objects.get(username=username)
                message = '用户名已被使用'
            except:
                user = User.objects.create(**form.cleaned_data)

        else:
            print(form.errors)
            message = form.errors

    return JsonResponse({"message": message})



#-------------------------仪表盘内容-----------------------------------#


@login_auth
def dashboard(request,**kwargs):

    username = kwargs['username']

    top5_sal_result = list(topSalary(dash_df))
    top5_offer_result = list(offerNumberIncreaseTop5InWeek(dash_df))
    avg_sal_this_month = list(avgSalryInMonth(dash_df))
    avg_sal_every_month = list(avgSalaryEveryMonth(dash_df))
    offer_change = list(offerNumberPercentChangeBetweenLastMonthAndThisMonth(dash_df))

    dic = {
        'username':username,
        'top5_sal_result':top5_sal_result,
        'top5_offer_result':top5_offer_result,
        'avg_sal_this_month':avg_sal_this_month,
        'avg_sal_every_month':avg_sal_every_month,
        'offer_change':offer_change
    }


    return render(request, 'big_data/dashboard/index.html', dic)



@csrf_exempt
@login_auth
def profile(request,**kwargs):#修改个人信息
    if request.method == 'GET':
        username = kwargs['username']
        userinfo = User.objects.get(username = username)

        password = userinfo.password
        email = userinfo.email
        phone = userinfo.phone
        profile = userinfo.profile

        dic={
            'username':username,
            'password':password,
            'email':email,
            'phone':phone,
            'profile':profile
        }

        return render(request,'big_data/dashboard/profile.html',dic)

    if request.method =='POST':
        message = '修改成功！'
        form = UserForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            email = form.cleaned_data['email']
            password = form.cleaned_data['password']
            phone = form.cleaned_data['phone']
            profile = form.cleaned_data['profile']

            user = User.objects.get(username=username)

            user.email = email
            user.password = password
            user.phone = phone
            user.profile = profile

            user.save()

            dic = {
                'email':email,
                'password':password,
                'phone':phone,
                'profile':profile,
                'message':message
            }

            return JsonResponse(dic)
        else:
            print(form.errors)
            message = form.errors

            return JsonResponse({"message": message})



#---------------------职业匹配-------------------------
@login_auth
def recommand(request,**kwargs):
    # 简历输入
    username = kwargs['username']
    user = User.objects.get(username=username)
    profile = user.profile

    dic={'jobinfo_n': jobinfo_n,
         'username':username,
         'profile':profile
         }

    if isinstance(request.GET.get("inputText"), str):
        # 职业匹配
        inputText = request.GET.get("inputText")
        app = Application()
        result = app.use_classification(text_string=str(inputText))

        # 提取表格
        unifyName = result
        page = 1
        if unifyName==None or unifyName == '非法输入':
            unifyName = 'Java'
        jobinfoDict, offers, total_pages = sendTableData(unifyName, page)

        # 能力分类词云
        label1WordList, label1CountList, label2WordList, label2CountList, label3WordList, label3CountList = getWordCloudData(unifyName)

        abilityDict = {
            'professionalWord': label1WordList,
            'professionalCount': label1CountList,
            'persionWord': label2WordList,
            'persionCount': label2CountList,
            'toolWord': label3WordList,
            'toolCount': label3CountList,
            'username': username

        }

        dict = {
            "result": result,
            "jobinfo": jobinfoDict,
            "unifyName": unifyName,
            "offers": offers,
            "page": page,
            "total_pages": total_pages,
            "abilityDict": abilityDict,
            'username': username

        }
        return JsonResponse(dict, safe=True)

    # 表格换页
    if isinstance(request.GET.get("newPage"), str):
        newPage = int(request.GET.get("newPage"))
        unifyName = str(request.GET.get("jobName"))
        page = newPage

        jobinfoDict, offers, total_pages = sendTableData(unifyName, page)

        dict = {
            "jobinfo": jobinfoDict,
            "unifyName": unifyName,
            "offers": offers,
            "page": page,
            "total_pages": total_pages,
            'username': username

        }
        return JsonResponse(dict, safe=True)

    return render(request, 'big_data/dashboard/jobmatch.html',dic)

def sendTableData(unifyName, page):
    ##根据前端传来的页值，得到数据的起始索引和接受索引
    per_page = 20## 定下一页的记录数
    start_data = (page - 1) * per_page
    end_data = page * per_page

    jobs = Jobinfo.objects.filter(unifyName=unifyName)  ## 根据url中的值过滤从数据库中抽出的记录
    offers = jobs.count()  ##记录条数
    jobinfo = jobs[start_data:end_data]  ## 制作记录切片

    total_pages, extra_page = divmod(offers, per_page)  ## 返回总共的页数和不满足一整页的记录数
    if extra_page != 0:  ## 如果有多余页,总页数加1
        total_pages += 1
    total_pages = range(1, total_pages+1)

    jobinfoDict = {
        'jobName': [],
        'company': [],
        'salary': [],
        'jobURL': []
    }

    if page == max(total_pages) and extra_page != 0:
        for num in range(extra_page):
            jobinfoDict["jobName"].append(jobinfo.values("jobName")[num]["jobName"])
            jobinfoDict["company"].append(jobinfo.values("company")[num]["company"])
            jobinfoDict["salary"].append(jobinfo.values("salary")[num]["salary"])
            jobinfoDict["jobURL"].append(jobinfo.values("jobURL")[num]["jobURL"])
    else:
        for num in range(int(per_page)):
            jobinfoDict["jobName"].append(jobinfo.values("jobName")[num]["jobName"])
            jobinfoDict["company"].append(jobinfo.values("company")[num]["company"])
            jobinfoDict["salary"].append(jobinfo.values("salary")[num]["salary"])
            jobinfoDict["jobURL"].append(jobinfo.values("jobURL")[num]["jobURL"])

    return jobinfoDict, offers, max(total_pages)


