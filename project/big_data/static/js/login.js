
function login(btn_id,span_id)
{

            var username=document.getElementsByClassName('form-control')[0].value;
            var password=document.getElementsByClassName('form-control')[1].value;

            if (username == "")
            {
                alert('用户名不能为空');
                return ;
            }
            if (password == "")
            {
                alert('密码不能为空');
                return;
            }

            $.ajax({
                type:"POST",
                dataType:"json",
                url:"login",
                data:{'username':username,'password':password},
                success:function(ret){
                    var message = ret.message;

                    if(message.password)
                    {
                        $(span_id).html(message.password);
                    }
                    else if(message == '登入成功！')
                    {
                        $(span_id).html(message);
                        window.location.href='/home';
                    }
                    else
                    {
                        $(span_id).html(message);
                    }




                }
            })


}

function to_register()
{
    window.location.href='/register';
}

function register(register_btn,username_span)
{

            var username = document.getElementsByClassName('form-control')[0].value
            var password = document.getElementsByClassName('form-control')[1].value
            var email = document.getElementsByClassName('form-control')[2].value

             if (username == "")
            {
                alert('用户名不能为空');
                return ;
            }
            if (password == "")
            {
                alert('密码不能为空');
                return;
            }
            if (email == "")
            {
                alert('邮箱不能为空');
                return
            }

            $.ajax({
                    type:"POST",
                    dataType:"json",
                    url:"register",
                    data:{'username':username,'password':password,'email':email},
                    success:function(ret){
                        var message = ret.message;


                        if(message == '注册成功！')
                        {
                            alert('注册成功！');
                            window.location.href='/home';
                        }
                        else if(message == '用户名已被使用')
                        {
                            $(username_span).html(message);
                        }
                        else
                        {
                            $(username_span).html(message.username);
                            $('#password_span').html(message.password);
                            $('#email_span').html(message.email);

                        }

                    }
                })

}

