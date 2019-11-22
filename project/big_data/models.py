from django.db import models


class Jobinfo(models.Model):
    jobName = models.CharField(db_column='jobName', primary_key=True, max_length=6)  # Field name made lowercase.
    company = models.CharField(max_length=30)
    address = models.CharField(max_length=10, blank=True, null=True)
    salary = models.CharField(max_length=10, blank=True, null=True)
    date = models.CharField(max_length=10, blank=True, null=True)
    exp = models.CharField(max_length=10, blank=True, null=True)
    edu = models.CharField(max_length=2, blank=True, null=True)
    offerNumber = models.CharField(db_column='offerNumber', max_length=4, blank=True, null=True)  # Field name made lowercase.
    jobInfo = models.CharField(db_column='jobInfo', max_length=50, blank=True, null=True)  # Field name made lowercase.
    companyType = models.CharField(db_column='companyType', max_length=10, blank=True, null=True)  # Field name made lowercase.
    staffNumber = models.CharField(db_column='staffNumber', max_length=10, blank=True, null=True)  # Field name made lowercase.
    companyOrientation = models.CharField(db_column='companyOrientation', max_length=5, blank=True, null=True)  # Field name made lowercase.

    class Meta:
        db_table = 'jobinfo'
        unique_together = (('jobName', 'company'),)

