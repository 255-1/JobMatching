from django.shortcuts import render
from .models import Jobinfo
from django.http import HttpResponse
# Create your views here.
import random
from big_data.analyse import count_Salary,get_data

def test(request):
    all_posts=Jobinfo.objects.all()[:5]
    
    return render(request,'big_data/base.html',{'posts':all_posts})

def get_jobinfo(request):
    '''
    查询前端表单发来的职业信息
    '''
    if request.method =='POST':
        try:
            jobName = request.POST.get("jobs")
            
            jobinfo=Jobinfo.objects.filter(jobName=jobName)
            offers=len(jobinfo)
            
            
            return render(request,'big_data/form.html',{"jobinfo":jobinfo,"offers":offers})

        except Exception as e:
            return HttpResponse("没找到你要找的社畜职业!")
    
def get_group_statistics(request):
    '''
    获得不同公司种类职业的最低，平均，最高工资
    '''
    data=get_data()
    option = request.POST.get("option")

    df=count_Salary(data,option)
    result=df.values
    companys=df.index

    results=zip(companys,result)
    

    return  render(request,'big_data/stats.html',{"results":results})