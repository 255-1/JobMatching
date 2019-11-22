from django.urls import path

from . import views

urlpatterns=[
    path('',views.test,name='test'),
    path('form',views.get_jobinfo,name='form'),
    path('stats',views.get_group_statistics,name='stats')
]