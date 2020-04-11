from django.db import models
from django.contrib.auth.models import AbstractUser

class Jobinfo(models.Model):
    jobName = models.CharField(db_column='jobName', max_length=250)  # Field name made lowercase.
    company = models.CharField(max_length=250)
    address = models.CharField(max_length=250)
    salary = models.CharField(max_length=250)
    date = models.CharField(max_length=250)
    exp = models.CharField(max_length=250)
    edu = models.CharField(max_length=250)
    offerNumber = models.CharField(db_column='offerNumber', max_length=250)  # Field name made lowercase.
    jobInfo = models.TextField(db_column='jobInfo')  # Field name made lowercase.
    companyType = models.CharField(db_column='companyType', max_length=250)  # Field name made lowercase.
    staffNumber = models.CharField(db_column='staffNumber', max_length=250)  # Field name made lowercase.
    companyOrientation = models.CharField(db_column='companyOrientation', max_length=250)  # Field name made lowercase.
    jobURL = models.CharField(db_column='jobURL', max_length=255)  # Field name made lowercase.
    unifyName = models.CharField(db_column='unifyName', max_length=255, blank=True, null=True)  # Field name made lowercase.

    class Meta:
        db_table = 'jobinfo'
        unique_together = (('jobName', 'company', 'date'),)

class User(AbstractUser):
    username = models.CharField(max_length=16,primary_key=True,verbose_name='用户名')
    password = models.CharField(max_length=128,verbose_name='密码')


    name = models.CharField(max_length=32, blank=True,verbose_name='真实姓名')
    sex = models.CharField(max_length=32,choices=[('male','男'),('female','女')],default='male',verbose_name="性别")
    age = models.IntegerField(null=True, blank=True,verbose_name='年龄')
    email = models.EmailField(blank=True,null=True,verbose_name='邮箱地址')
    phone = models.BigIntegerField(blank=True, null=True,verbose_name='手机号码')
    address = models.CharField(max_length=128, blank=True,verbose_name='工作地址')
    workingYear = models.CharField(max_length=32,blank=True,verbose_name='工作年龄')
    salaryWanted = models.FloatField(null=True,verbose_name='期望薪水')
    blog = models.URLField(blank=True,null=True,verbose_name='博客地址')
    edu = models.CharField(max_length=16, blank=True,verbose_name='教育水平')
    glory = models.TextField(blank=True,verbose_name='获得荣耀')
    exp = models.TextField(blank=True,verbose_name='工作经验')
    description = models.TextField(blank=True,verbose_name='个人经历')
    professioanlSkill = models.CharField(max_length=256,blank=True,verbose_name='专业技能')
    personalSkill = models.CharField(max_length=256, blank=True, verbose_name='个人技能')
    toolSkill = models.CharField(max_length=256, blank=True, verbose_name='工具使用技能')


    def __str__(self):
        return self.username

    class Meta:
        db_table='user'
        verbose_name = '用户信息'
        verbose_name_plural = '用户信息'





