+ 修改了User之前字段的一些定义。允许一些字段可以取null值。
+ 增加了**sex**，**salaryWanted**字段。salaryWanted是一个float字段，单位为万/月。
+ 所有的内容直接覆盖掉就可以了
+ 需要重新migrate数据库



forms.py	>	/big_data/forms.py

models.py	>	/big_data/models.py

profile.html	>	/big_data/templates/big_data/dashboard/profile.html

profile.css	>	/big_data/static/css/dashboard/profile.css

---

简历下载需要库

jinja2     2.11.1

docxtpl   0.6.4(前置库docx pip应该会把依赖解决掉)