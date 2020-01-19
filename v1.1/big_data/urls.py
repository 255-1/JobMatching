from django.urls import path,re_path

from . import views

urlpatterns=[
    path('',views.home,name='home'),
    path('<str:unifyName>/<int:page>',views.get_jobinfo,name='unifyName'),
    path('salary/<str:unifyName>',views.get_group_statistics,name='salary'),
    path('detail',views.detail,name='detail'),
  
    path("groupByOneFeature",views.get_groupByOneFeature,name="get_groupByOneFeature"),
    path("groupByTwoFeatures",views.get_groupByTwoFeatures,name="get_groupByTwoFeatures"),
    re_path('.*',views.page_not_found)
]


