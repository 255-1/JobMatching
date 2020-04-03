from django.shortcuts import render, redirect,HttpResponse
from .models import *
from big_data.static import *
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .forms import  *
from big_data.map import *
from .ClassifierJobNLP.ClassifierJobNameApp import Application
from .AbilityWordCloudNLP.wordCloud import getWordCloudData
import os
import re
from big_data.dashBoard import *
from django.contrib.auth import authenticate
from django.contrib.auth import login as dj_login
import datetime
from django.contrib.auth.hashers import make_password, check_password
from big_data.AbilityWordCloudNLP.classification import *


jobinfo=Jobinfo.objects.all()##返回所有数据库内记录
data=pd.DataFrame(list(jobinfo.values()))##返回所有数据库内记录，数据结构为df
jobinfo_n=Jobinfo.objects.count() ##所有数据库内记录的数量，单位为万


date_today = datetime.datetime.now()
date_30days_ago = today - datetime.timedelta(days = 30)
date_60days_ago = today - datetime.timedelta(days = 60)


f_todayDate = date_today.strftime('%Y-%m-%d')
f_date_30days_ago= date_30days_ago.strftime('%Y-%m-%d')
f_date_60days_ago = date_60days_ago.strftime('%Y-%m-%d')

# f_todayDate = "2020-03-02"
# f_date_30days_ago = "2020-02-02"
# f_date_60days_ago = "2020-01-02"##调试用


dash_df = getDashBoardDataToDF(jobinfo)##dashboard.py用df
dash_df_thisPeriod = dash_df[(dash_df["date"] <= f_todayDate) & (dash_df["date"] >= f_date_30days_ago)]##这个月前30天的所有记录
dash_df_lastPeriod = dash_df[(dash_df["date"] <= f_date_30days_ago) & (dash_df["date"] >= f_date_60days_ago)]#30天前到60天前的所有记录

today_fetch = Jobinfo.objects.filter(date = f_todayDate).count()

#----------工具方法----------------#
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

def get_session_value(request,key):
    '''
    获得session中保留的值
    :param key: str session的键
    :return: key对应的值
    '''
    if request.session.get(key,None):
        value = request.session[key]
    else:
        value = None
    return value

def page_not_found(request):##处理未知域名
    return render(request,'big_data/404.html')

def home(request):
    '''
    家页面
    '''

    username = get_session_value(request,'username')##判断用户是否已经登陆
    is_admin = get_session_value(request,'is_admin') ##是否为管理员

    #为了调试方便 关闭地图
    areaList, countList = getMapData()
    dic = {
           'jobinfo_n':jobinfo_n,
           'username':username,
           'areaList': areaList,
           'is_admin':is_admin,
           'countList': countList}

    # dic = {
    #     'jobinfo_n': jobinfo_n,
    #     'username': username,
    #     'is_admin': is_admin,
    #     }

    return render(request,'big_data/home.html',dic)





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


                if not authenticate(username = username,password = password):
                    message = '密码错误'
                else:
                    is_superuser = user.is_superuser
                    if is_superuser:
                        dj_login(request, user)
                        request.session['is_login'] = True
                        request.session['username'] = username
                        request.session['is_admin'] = True
                        return JsonResponse({"message": message})
                    else:
                        request.session['is_login'] = True
                        request.session['username'] = username
                        request.session['is_admin'] = False

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
                password = form.cleaned_data['password']
                form.cleaned_data['password'] = make_password(password)
                user = User.objects.create(**form.cleaned_data)

        else:
            print(form.errors)
            message = form.errors

    return JsonResponse({"message": message})



#-------------------------仪表盘内容-----------------------------------#


@login_auth
def dashboard(request,**kwargs):

    username = kwargs['username']

    op = "edu"  ##设定groupByOneFeatrue的默认值
    lab, val = groupByOneFeature(data, op)

    op2 = ["jobName", "edu"]  ##设定groupByTwoFeatures的默认值
    lab2, val2 = groupByTwoFeatures(data, op2)

    ofs_w = offersInWeek(data)

    cmp_o = companyOrientationDistribution(data)



    top5_sal_result = np.array(list(topSalary(dash_df_thisPeriod)))
    top5_job_name = top5_sal_result[0].tolist()#平均工资前5的职业名
    top5_sal_result_lastMonth = list(get_msal_and_offer(dash_df_lastPeriod,top5_job_name))#获得上一个周期这五个职业的薪水和offer数

    this_period = pd.DataFrame(top5_sal_result[1:].astype('float64'), columns=top5_sal_result[0])#这个周期的数据转换为df
    last_period = pd.DataFrame(top5_sal_result_lastMonth[1:], columns=top5_sal_result_lastMonth[0])

    top5_sal_diff = [ (this_period[col]-last_period[col]).tolist() for col in top5_job_name]#这个周期和上个周期的薪水，offer数差值

    top5_offer_result = np.array(list(offerNumberIncreaseTop5InWeek(dash_df_thisPeriod,dash_df_lastPeriod)))
    top5_offer_diff = top5_offer_result.tolist()[1]

    top5_sal_result= top5_sal_result.tolist()#将结果转回列表
    top5_offer_result= top5_offer_result.tolist()#将结果转回列表

    avg_sal_this_month = list(avgSalryInMonth(dash_df))
    avg_sal_every_month = list(avgSalaryEveryMonth(dash_df))
    offer_change = list(offerNumberPercentChangeBetweenLastMonthAndThisMonth(dash_df))

    date = Jobinfo.objects.values("date")
    start_day = date[0]["date"]
    end_day = date[(len(date)-1)]["date"]

    dic = {
        "lab": lab,
        "val": val,
        "lab2": lab2,
        "val2": val2,
        "ofs_w": ofs_w,
        "cmp_o": cmp_o,
        'username':username,
        'top5_sal_result':top5_sal_result,
        'top5_offer_result':top5_offer_result,
        'avg_sal_this_month':avg_sal_this_month,
        'avg_sal_every_month':avg_sal_every_month,
        'offer_change':offer_change,
        'top5_sal_diff':top5_sal_diff,
        'top5_offer_diff':top5_offer_diff,
        'today_fetch':today_fetch,
        'jobinfo_n':jobinfo_n,
        'start_day':start_day,
        'end_day':end_day,
    }


    return render(request, 'big_data/dashboard/index.html', dic)


@login_auth
def get_jobinfo(request, **kwargs):
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

    per_page = 20  # 定下一页的记录数
    start_data = (page - 1) * per_page
    end_data = page * per_page

    jobs = Jobinfo.objects.filter(unifyName=unifyName, date="2020-03-02")  ##根据url中的值过滤从数据库中抽出的记录
    ##这里如果我没记错，应当取最新一天的数据，这里为了调试方便取一个定值

    offers = jobs.count()  ##记录条数
    job_dices = jobs[start_data:end_data]  ##制作记录切片

    total_pages, extra_page = divmod(offers, per_page)  ##返回总共的页数和不满足一整页的记录数
    if extra_page:  ##如果有多余页,总页数加1
        total_pages += 1
    total_pages = range(1, total_pages + 1)
    # ----------------------------------------以上为v2.3以前的jobinfo代码---------------------------
    # v2.3修改错误变量名
    option = kwargs['option']
    option_dc = {'edu': '教育背景',
                 'exp': '工作经验',
                 'companyType': '公司类型',
                 'address': '地区',
                 'companyOrientation': '公司领域'}  ##选项转换字典

    df = count_Salary(pd.DataFrame(list(jobs.values())), option)  ##调用模块
    result = [df.iloc[:5, i].values.tolist() for i in range(3)]
    lab = list(df.index)[:5]
    lab = [re.sub(r'[\(\[\)\]]', '', i) for i in lab]  ##去掉一些不必要的符号
    # v2.3重新处理公司方向
    if option == 'companyOrientation':
        # 减少字符串长度
        for i in range(len(lab)):
            cOList = lab[i].split(',')
            if (len(cOList) > 1):
                lab[i] = cOList[1]
            else:
                lab[i] = cOList[0][0:5]

    # #这地方跟数据有关,可能要取不同的分割符
    option = option_dc[option]  ##把选项转换位中文

    # ----------------------------------------以上为v2.3修改并合并的get_group_statistics代码--------------------------------------
    # 以职位为分组的地图数据
    areaList, countList = getMapDataByUnifyName(kwargs['unifyName'])
    #获取平均工资
    avgSalary = '{:.2f}'.format(np.average(df['avgSalary']))

    dic = {"job_dices": job_dices,
           "offers": offers,
           "unifyName": unifyName,
           "total_pages": total_pages,
           "page": page,
           "per_page": per_page,
           'jobinfo_n': jobinfo_n,
           'uName': uName,
           'special': special,
           'username': username,
           # v2.3转移原get_group_statistics中统计信息
           'lab': lab,
           "data": result,
           "option": option,
           # v2.3添加地图数据
           'areaList': areaList,
           'countList': countList,
           'avgSalary': avgSalary}

    return render(request, 'big_data/dashboard/jobinfo.html', dic)



def get_groupByOneFeature(request): ## 响应ajax的请求，返回用户选择后筛选后的值

    option=request.GET.get("option",0)
    result=groupByOneFeature(data,option)

    return JsonResponse({"result":result,'jobinfo_n':jobinfo_n})


@csrf_exempt ##跳过跨域检查
def get_groupByTwoFeatures(request): ## 响应ajax的请求，返回用户选择后筛选后的值

    option=request.POST.getlist("option",[])
    result=groupByTwoFeatures(data,option)

    return JsonResponse({"result":result,'jobinfo_n':jobinfo_n})

@csrf_exempt
@login_auth
def profile(request,**kwargs):#修改个人信息
    if request.method == 'GET':
        username = kwargs['username']
        userinfo = User.objects.get(username = username)

        dic = userinfo.__dict__  ##将用户信息转换为字典
        dic.pop('_state')##去除字典里不必要的字段

        return render(request,'big_data/dashboard/profile.html',dic)

    if request.method =='POST':
        message = '修改成功！'
        form = UserForm(request.POST)
        if form.is_valid():##表单验证

            form_data = form.cleaned_data##获得表单数据
            username = form_data['username']

            password = User.objects.get(username = username).password#原密码

            if form_data['password'] == password:##如果表单传来的密码和原密码相同，则不需要修改密码
                pass
            else:
                form_data['password'] = make_password(form_data['password'])



            info = form_data['glory']+","+form_data['exp']+","+form_data['description']
            mode = re.compile(r'\n')
            info = mode.sub(',', info)

            dict_info = {'jobInfo':[info]}
            df = pd.DataFrame(dict_info)

            cwd = os.getcwd()+"\\big_data\\model"

            modelPath = os.path.join(cwd, 'SVM.model')
            stopWordsPath = os.path.join(cwd, 'stopwords.txt')
            addWordsPath = os.path.join(cwd, 'addwords.txt')
            wordsIdPath = os.path.join(cwd, 'words_id.txt')

            classifier = JobinfoClassifier(modelPath, df, stopWordsPath, addWordsPath, wordsIdPath)
            result = classifier.predict()

            professionalSkill = '\n'.join(result[result['分类'] == 1]['句子'].values)
            personalSkill = '\n'.join(result[result['分类'] == 2]['句子'].values)
            toolSkill = '\n'.join(result[result['分类'] == 3]['句子'].values)

            form_data['professioanlSkill'] = professionalSkill
            form_data['personalSkill'] = personalSkill
            form_data['toolSkill'] = toolSkill



            User.objects.filter(username=username).update(**form_data)##利用表单数据更新用户数据

            dic = form_data
            dic.update({'message':message})

            return JsonResponse(dic)
        else:
            print(form.errors)
            message = form.errors##将错误的消息返回给前端

            return JsonResponse({"message": message})



#---------------------职业匹配-------------------------
@login_auth
def recommand(request, **kwargs):
    # 简历输入
    username = kwargs['username']
    user = User.objects.get(username=username)
    profile = user.exp + user.glory + user.description

    dic = {'jobinfo_n': jobinfo_n,
         'username': username,
         'profile': profile
         }

    # 将用户信息作为推荐简历筛选条件
    filterOption = []
    user = User.objects.get(username=username)

    if user.workingYear or user.edu or user.address:
        filterOption.append(user.workingYear)
        filterOption.append(user.edu)
        filterOption.append(user.address)
        filterOption.append(user.age)
    print(filterOption)

    if isinstance(request.GET.get("inputText"), str):
        # 职业匹配
        inputText = request.GET.get("inputText")
        print('输入内容')

        textList = inputText.splitlines()
        resultList = []

        if len(textList) < 10:
            # print(textList)
            if len(textList) == 0:
                resultList.append('非法输入')
                resultList.append('非法输入')
                resultList.append('非法输入')
            else:
                for text in textList:
                    app = Application()
                    resultList.append(app.use_classification(text_string=str(text)))
        else:
            import random
            for num in range(20):
                # 随机70%进行职业匹配
                randomText = random.sample(textList, int(0.7 * len(textList)))
                textString = ''
                for i in range(len(randomText)):
                    textString = textString + randomText[i]
                app = Application()
                resultList.append(app.use_classification(text_string=str(textString)))

        resultCountList = []
        # 若非法输入元素大于2，则判断为非法输入
        if resultList.count('非法输入') > 2 or resultList == ['非法输入']:
            resultCountList.append('非法输入')
        else:
            removeNum = 0
            while '非法输入' in resultList:
                resultList.remove('非法输入')
                removeNum = removeNum + 1
            sum = len(resultList) - removeNum
            # 统计职业匹配结果，生成字典
            import collections
            countDict = collections.Counter(resultList)
            # 计算占比
            for key, value in countDict.items():
                countDict[key] = round(value * 100 / len(resultList), 2)
            # 字典按value排序，生成list
            sortedCountList = sorted(countDict.items(), key=lambda x: x[1], reverse=True)
            for num in range(len(sortedCountList)):
                resultCountList.append(sortedCountList[num][0])
                resultCountList.append(sortedCountList[num][1])

        print(resultCountList)

        # 提取表格
        unifyName = resultCountList[0]
        page = 1
        if unifyName == '非法输入':
            unifyName = 'Java'


        jobinfoDict, offers, total_pages = sendTableData(unifyName, page, filterOption)
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
            "result": resultCountList,
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

        jobinfoDict, offers, total_pages = sendTableData(unifyName, page,filterOption)

        dict = {
            "jobinfo": jobinfoDict,
            "unifyName": unifyName,
            "offers": offers,
            "page": page,
            "total_pages": total_pages,
            'username': username

        }
        return JsonResponse(dict, safe=True)

    return render(request, 'big_data/dashboard/jobmatch.html', dic)



def sendTableData(unifyName, page, filterCondition=None):
    ##根据前端传来的页值，得到数据的起始索引和接受索引
    per_page = 20## 定下一页的记录数
    start_data = (page - 1) * per_page
    end_data = page * per_page

    jobs = Jobinfo.objects.filter(unifyName=unifyName)

    if filterCondition[0] or filterCondition[1] or filterCondition:
        # 筛-经验exp
        if filterCondition[0]:
            expFloorList = ['无工作经验', '无需经验']
            for jobExp in list(Jobinfo.objects.distinct().values('exp')):
                if filterCondition[0][0] != '无' and jobExp['exp'][0] <= filterCondition[0][0] and jobExp['exp'][0] != '无':
                    expFloorList.append(jobExp['exp'])
            if filterCondition[0][0] != '无' and filterCondition[0][0:1] != '10':
                expFloorList.remove('10年以上经验')

            print('经验过滤要求包含以下列表内容：')
            print(expFloorList)
            jobs = jobs.filter(exp__in=expFloorList)
        # 筛-学历
        if filterCondition[1]:
            eduFloorList = ['没有要求', '高中', '大专', '本科', '硕士', '博士']
            num = eduFloorList.index(filterCondition[1])
            sum = len(eduFloorList)
            for j in range(sum - num - 1):
                eduFloorList.remove(eduFloorList[-1])
            print('学历过滤要求包含以下列表内容：')
            print(eduFloorList)
            jobs = jobs.filter(edu__in=eduFloorList)
        # 筛-地址
        if filterCondition[2]:
            print('地址为：')
            print(filterCondition[2])
            jobs = jobs.filter(address__startswith=filterCondition[2])
            # jobs = jobs.create(address__startswith='m美国')

    print('筛选后去重结果：')
    print(jobs.count())
    print(jobs.distinct().values('exp'))
    print(jobs.distinct().values('edu'))
    print(jobs.distinct().values('address'))

    if jobs.count() == 0:
        offers = 1
        total_pages = range(1, 2)
        jobinfoDict = {
            'jobName': [],
            'company': [],
            'salary': [],
            'jobURL': []
        }
        jobinfoDict["jobName"].append('建议【个人信息】界面提升简历')
        jobinfoDict["company"].append('无')
        jobinfoDict["salary"].append('无')
        jobinfoDict["jobURL"].append('无')

    else:
        # 职业匹配
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



