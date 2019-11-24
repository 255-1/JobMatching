from django.db import models

# Create your models here.

class JobInfo(models.Model):
    jobName = models.CharField(db_column='jobName', primary_key=True, max_length=6)
    company = models.CharField(max_length=50)
    address = models.CharField(max_length=10, blank=True, null=True)
    salary = models.CharField(max_length=10, blank=True, null=True)
    date = models.CharField(max_length=10, blank=True, null=True)
    exp = models.CharField(max_length=10, blank=True, null=True)
    edu = models.CharField(max_length=2, blank=True, null=True)
    offerNumber = models.CharField(db_column='offerNumber', max_length=4, blank=True, null=True)
    jobInfo = models.CharField(db_column='jobInfo', max_length=50, blank=True, null=True)
    companyType = models.CharField(db_column='companyType', max_length=10, blank=True, null=True)
    staffNumbe = models.CharField(db_column='staffNumber', max_length=20, blank=True, null=True)
    companyOrientation = models.CharField(db_column='companyOrientation', max_length=5, blank=True, null=True)
