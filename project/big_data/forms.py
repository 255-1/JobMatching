from django import  forms


##User，注册用验证表单
class RegisterForm(forms.Form):
    username = forms.CharField(label='username', max_length=16)
    password = forms.CharField(label='password', max_length=32, min_length=8,widget=forms.PasswordInput, \
                               error_messages={'min_length':'密码至少要8位及以上'})
    email = forms.EmailField(error_messages={'required':'邮箱不能为空','invalid':'邮箱格式不对'})
##User，用户信息更新用表单
class UserForm(forms.Form):
    username = forms.CharField(label='username')
    password = forms.CharField(label='password', max_length=128, min_length=8, widget=forms.PasswordInput, \
                               error_messages={'min_length': '密码至少要8位及以上'})
    name = forms.CharField(label='name',required=False,max_length=32,error_messages={'max_length':'姓名过长'})
    age = forms.IntegerField(label='age',required=False,error_messages={'invalid':'年龄应该是一个整数'})
    sex=forms.CharField(label='sex',required=False)
    email = forms.EmailField(label='email',required=False,error_messages={'invalid':'邮箱格式不对'})
    phone = forms.IntegerField(label='phone',required=False,error_messages={'invalid':'请输入正确的联系方式'})
    blog = forms.URLField(label='blog',required=False,error_messages={'invalid':'请输入正确的博客地址'})
    address = forms.CharField(label='address',required=False,max_length=128,error_messages={'invalid':'地址过长'})
    workingYear = forms.CharField(label='workingYear',max_length=32,required=False,error_messages={'invalid':'年龄输入有误,你小子在干坏事'})
    edu = forms.CharField(label='edu',max_length=16,required=False,error_messages={'invalid':'教育水平输入有误'})
    glory = forms.CharField(label='glory',required=False)
    exp = forms.CharField(label='exp',required=False)
    description = forms.CharField(label='description',required=False)
    salaryWanted = forms.FloatField(min_value=0,required=False,error_messages={'invalid':'请输入一个正确的期望薪水值！'})