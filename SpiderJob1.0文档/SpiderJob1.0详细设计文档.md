#Spider1.0详细设计文档
##1.支撑环境
>Java1.7+  
>UTF-8字符集  
>maven 3.6.1+  
>maven pom.xml配置参数  
>
 	<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.13</version>
	</dependency>
    <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>3.8.1</version>
    <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/com.alibaba/fastjson --> 
    <dependency> 
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.47</version> 
    </dependency> 
    <!-- https://mvnrepository.com/artifact/ch.hsr/geohash --> 
    <dependency> 
    <groupId>ch.hsr</groupId> 
    <artifactId>geohash</artifactId> 
    <version>1.3.0</version> 
    </dependency> 
    <!-- https://mvnrepository.com/artifact/commons-httpclient/commons-httpclient --> 
    <dependency> 
    <groupId>commons-httpclient</groupId> 
    <artifactId>commons-httpclient</artifactId> 
    <version>3.1</version> </dependency> 
    <!-- https://mvnrepository.com/artifact/org.jsoup/jsoup --> 
    <dependency> <groupId>org.jsoup</groupId> 
    <artifactId>jsoup</artifactId> 
    <version>1.8.3</version> 
    </dependency>


##2.类设计详情
###2.1 基本类
***JobBean***  
* 作用:流程中的基本单位，记录了职位名称，公司名，地址，工资，发布日期，经验要求，学历要求，招聘人数，职位信息，公司类型，公司人数，公司方向  
* Function:  
>toString()  定义输出格式  
>saveString() 定义保存格式，间隔4个空格  

***ConnectMySQL***  
* 作用:加载驱动以及连接Mysql数据库   
* 注意事项:需要预先写入数据库名称，用户名，密码
*Function:  
>LoadDriver()  加载mysql驱动  
>ConnectMysql() 连接数据库  
>getConn()   
>>**Output 数据库的连接**

###2.2全局静态工具类   
***DBUtils***   
作用:提供JobBean和MySQL数据库之间的读存操作  
Function:  
>insert(Connection conn,String tableName,List<JobBean\> jobBeanList)  
>>**Input 数据库的连接，表名，JobBean的列表**  
>
>select（Connection conn, String tableName）  
>>**Input 数据库的连接，表名**  
>>**Output JobBean列表**

***JobBeanUtils***
作用:提供JobBean和本地文件之间的读存操作  
Function:  
>SaveJobBeanList(List<JobBean\> jobBeanList, String fileName)
>>**Input JobBean列表， 本地文件名**  

>LoadJobBeanList(String fileName)  
>>**Input 本地文件名**
>>**Output JobBean列表**  
###2.3业务类
***SpiderURL***  
作用：爬取带有关键字的职业的URL  
Input:51job上“大数据+上海”搜索的URL，职位名关键字  
Function:  
>GetDom(String strURL) 
>>**Input 网页url**  
>>**Output 网页全部信息**

>GetPageInfo(Document document)
>>**Input 网页全部信息**  
>>**Output 筛选了关键字的URL**

>GetNextPageURL(Document document)
>>**Input 网页全部信息**  
>>**Output 下一页的URL**

>SpiderURL()  获取全部，筛选关键字，URL存入列表，获取下一页  

>GetUrlList() 获取爬取的职位名带有关键字的URL列表

***SpiderJob***  
作用:爬取符合职位名筛选后的URL中的信息  
Input:筛选后的URL列表，职位信息关键字  
Function  
>GetDom(String strURL) 
>>**Input 网页url**  
>>**Output 网页全部信息**

>GetPageInfo(Document document)
>>**Input 网页全部信息**
>>**Output 筛选了职位信息关键字的JobBean**  

>isValide(JobBean jobBean)
>>**Input JobBean对象**  
>>**Output 关键字判断**

>SpiderJob() 获取全部，筛选关键字，JobBean存入列表  
>GetJobBeanList()
>>**Output 筛选了职位信息JobBean列表**

***Clean***  
作用:大数据职业的字段清理  
Input:JobBean列表  
Function:  
>InitDate() 初始化时间为近三天   
>StartClean() 字段处理  
>GetJobBeanList()  
>>**Output 清理完的列表**

####2.4主类
***Main***
作用:连通所有功能，实现流程功能
>>**Input 51job上“大数据+上海”搜索的URL**  
>>**Output 清洗过的本地文件和表，未清洗过的本地文件和表**