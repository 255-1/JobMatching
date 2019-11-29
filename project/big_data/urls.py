from django.urls import path

from . import views

urlpatterns=[
    path('home',views.home,name='home'),
    path('<str:jobName>/<int:page>',views.get_jobinfo,name='jobinfo'),
    path('stats',views.get_group_statistics,name='stats'),
]