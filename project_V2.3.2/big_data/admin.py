from django.contrib import admin
from .models import *

# Register your models here.


admin.site.site_title = '用户管理'
admin.site.site_header = '职业匹配系统'
admin.site.index_title = '后台'
#
class UserProfile(admin.ModelAdmin):
    search_fields = ['username','name']

    list_filter = ['edu','workingYear']

    list_display = ['username','age','edu','workingYear',]

admin.site.register(User,UserProfile)