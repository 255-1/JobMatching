from django import  forms



class RegisterForm(forms.Form):
    username = forms.CharField(label='username', max_length=16)
    password = forms.CharField(label='password', max_length=32, min_length=8,widget=forms.PasswordInput, \
                               error_messages={'min_length':'密码至少要8位及以上'})
    email = forms.EmailField(error_messages={'required':'邮箱不能为空','invalid':'邮箱格式不对'})

class UserForm(forms.Form):
    username = forms.CharField(label='username')
    password = forms.CharField(label='password', max_length=32, min_length=8, widget=forms.PasswordInput, \
                               error_messages={'min_length': '密码至少要8位及以上'})
    email = forms.EmailField(required=False,error_messages={'invalid':'邮箱格式不对'})
    phone = forms.IntegerField(label='phone',required=False,error_messages={'invalid':'请输入正确的联系方式'})
    profile = forms.CharField(max_length=500,required=False)

