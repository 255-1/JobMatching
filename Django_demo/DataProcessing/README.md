## 该模块为字段分析模块
> &ensp; i. 关于‘DataProcessing’目录  
> &ensp; ii. 部分字段分析模块来自**PYH**  
---

0. 程序运行环境：  
    Name | Version  
    -|-
    python | 3.7.4  
    pymysql | 0.9.3  
    numpy | 1.17.3  
    pandas | 0.25.3  


1. 文件夹需配合BigData使用，将2个文件放在同一父目录下；
2. 该文件目录tree详见父目录README.md文件；
3. 重要文件GetSearch.py信息：  
    + class GetSearch(object):  得到用户搜索内容，并根据内容分类。
        + def __init__(self, search_content, job_info):     获取用户搜索文本和数据库信息
        + def classifying(self): 对用户输入信息分类
    + def count_Salary(df): 计算各条职业的最低工资，平均工资，最高工资
    + def analysis_dict(attribute,df): 将df转换成dict输出
    + def analysis_json(attribute,df): 将df转换成json输出
    + def avg(a,b): 算均值


