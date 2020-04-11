from django.urls import path,re_path
from django.conf.urls import url
from . import views

urlpatterns=[
    path('',views.welcome,name='welcome'),
    path('home',views.home,name='home'),
    path('dashboard', views.dashboard, name='dashboard'),
    path('dashboard/jobmatch', views.recommand, name='recommand'),
    path('dashboard/profile',views.profile,name='profile'),
    path('dashboard/profileDownload', views.profileDownload, name='profileDownload'),
    path('dashboard/jobinfo/<str:unifyName>/<int:page>/<str:option>',views.get_jobinfo,name='unifyName'),
    path("groupByOneFeature",views.get_groupByOneFeature,name="get_groupByOneFeature"),
    path("groupByTwoFeatures",views.get_groupByTwoFeatures,name="get_groupByTwoFeatures"),
    path('login',views.login,name='login'),
    path('logout',views.logout,name='logout'),
    path('register',views.register,name='register'),
    re_path('.*',views.page_not_found)
]


