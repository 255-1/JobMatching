from django.db import models


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

