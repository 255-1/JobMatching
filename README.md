# JobMatching
实践项目，Java爬取职位信息，应用服务器Django处理业务逻辑，nlp处理职位信息，前端页面可视化职位相关信息以及职位匹配功能

# 运行使用

0.准备
```
MySQL 5.6+
Django 2.1.1+
Java 8+ 
Maven 3.6+
python的第三方包:如sklearn，docxtpl,numpy,jieba等
``` 
1.下载此项目
```
git clone https://github.com/255-1/JobMatching.git
```

2.先进入 Spider51Job文件夹，根据里面的README信息进行数据库DDL以及数据的准备,最好要准备60天的数据，可以邮箱联系创始人获取基础数据，然后启动爬虫项目的定时任务自行爬取数据  

3.进入projectV2.3.2文件夹，进行django的数据库迁移，首先修改project下setting中的DATABASES数据库名字和密码，其次big_data/migrations只保留__init__.py,其余py文件都删除，然后进行迁移，打开终端输入
```
python manage.py makemigrations
```
创建成功后执行
```
python manage.py migrate
```
如果执行上面这一句，出现“django.db.utils.InternalError: (1050, "Table 'jobinfo' already exists")”的错误  
删除big_data/migrations/0001_initial.py中operations下name是jobinfo的migrations.CreateModel，需要删除内容如下，删除完重新执行上面的语句

```
migrations.CreateModel(
            name='Jobinfo',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('jobName', models.CharField(db_column='jobName', max_length=250)),
                ('company', models.CharField(max_length=250)),
                ('address', models.CharField(max_length=250)),
                ('salary', models.CharField(max_length=250)),
                ('date', models.CharField(max_length=250)),
                ('exp', models.CharField(max_length=250)),
                ('edu', models.CharField(max_length=250)),
                ('offerNumber', models.CharField(db_column='offerNumber', max_length=250)),
                ('jobInfo', models.TextField(db_column='jobInfo')),
                ('companyType', models.CharField(db_column='companyType', max_length=250)),
                ('staffNumber', models.CharField(db_column='staffNumber', max_length=250)),
                ('companyOrientation', models.CharField(db_column='companyOrientation', max_length=250)),
                ('jobURL', models.CharField(db_column='jobURL', max_length=255)),
                ('unifyName', models.CharField(blank=True, db_column='unifyName', max_length=255, null=True)),
            ],
            options={
                'db_table': 'jobinfo',
                'unique_together': {('jobName', 'company', 'date')},
            },
        ),
```
4.运行项目  
```
python manage.py runserver
```