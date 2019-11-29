from django.db import models


class Jobinfo(models.Model):
    jobName = models.CharField(db_column='jobName', max_length=6)  # Field name made lowercase.
    company = models.CharField(max_length=50)
    address = models.CharField(max_length=10)
    salary = models.CharField(max_length=10)
    date = models.CharField(max_length=10)
    exp = models.CharField(max_length=10)
    edu = models.CharField(max_length=2)
    offerNumber = models.CharField(db_column='offerNumber', max_length=4)  # Field name made lowercase.
    jobInfo = models.CharField(db_column='jobInfo', max_length=70)  
    companyType = models.CharField(db_column='companyType', max_length=10)  # Field name made lowercase.
    staffNumber = models.CharField(db_column='staffNumber', max_length=20)  # Field name made lowercase.
    companyOrientation = models.CharField(db_column='companyOrientation', max_length=5)  # Field name made lowercase.

    class Meta:
        db_table = 'jobinfo'
        unique_together = (('jobName', 'company'),)

