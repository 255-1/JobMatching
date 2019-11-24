# -*- coding: utf-8 -*-

from django.shortcuts import render
from django.views.decorators import csrf
from JobModel.models import JobInfo
from django.forms.models import model_to_dict
import pandas as pd

from DataProcessing import GetSearch
from DataProcessing.GetSearch import *


# 接收POST请求数据
def search_post(request):
    '''

    :param request: 接收POST请求数据
    :return:
    '''
    ctx = {}        # 输入信息
    # 判断是否输入信息
    if request.POST:
        # 将Django QuerySet转换为pandas DataFrame
        df = pd.DataFrame(list(JobInfo.objects.all().values()))
        # print(df)

        a = GetSearch(request.POST['q'], df)

        a.classifying()
        print(a.classifying())
        ctx['rlt'] = a.classifying()

    return render(request, "post.html", ctx)