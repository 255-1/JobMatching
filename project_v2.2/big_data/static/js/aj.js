function aj_grpByone(clk_id,id,url,myChart,title)
{
    /*
      ajax get请求，用于给echart的图表修改设定值

      clk_id：点击元素(按钮，选择)的id str
      id：修改值的元素id str
      url：后端处理函数对应的url值 str
      myChart：要修改的目标图表 object
      title：图表的标题 str
    */

    $(document).ready(function(){
        $(clk_id).change(function(){  //当按钮的值改变时执行该函数
          var option = $(id).val(); //获得按钮的值
          
          myChart.showLoading(); //在图表设定没结束时，让图表处于加载状态动画
          $.get(url, {"option":option},function(ret){
 
              var l=ret.result[0];
              var v=ret.result[1];

              var t=title;
              
              myChart.hideLoading();
              myChart.setOption({
                  series : [
                      {
                          name: 'offer',
                          type: 'bar',    
                          data:v 
                      }
                  ],
                  xAxis: 
                  {
                   
                    data:l,
                
                  },
                  title:{
                    text:title,
                    left:100,
                },
                legend:{
                    show:true,
                    left:200,
                    top:'top'
                },
                  grid: {  //这里因为图表标签有些过长，必须修改grid的参数使其全部展示出来
                    show:true,
                    borderColor:"#c45455",
                    left:"20%",//grid 组件离容器左侧的距离。
                    right:"30px",
                    bottom:"40%"
                    },
              })
          })
        });
      });
}



function aj_grpBytwo(clk_id,id1,id2,url,myChart,title)
{
   /*
      ****
      ajax 请求，用于给echart的图表修改设定值

      clk_id：点击元素(按钮，选择)的id str
      id1：修改值的元素id1 str
      id2：修改值的元素id2 str
      url：后端处理函数对应的url值 str
      myChart：要修改的目标图表 object
      title：图表的标题 str
    */  
  $(document).ready(function(){
    $(clk_id).change(function(){
      var option1 = $(id1).val();
      var option2=$(id2).val();

      if (option1 == option2)
      {
          alert("请选择2个不一样选项来进行查询！");
          return ;
      }
      else
      {
          var option=[option1,option2];
      
          myChart.showLoading();

          $.ajax({
              type:"POST",
              url:url,
              traditional:true,
              data:{"option":option},
              dataType:"json",
              success:function(ret){
                  var l=ret.result[0];
                  var v=ret.result[1];

                  var t=title;
    
                  myChart.hideLoading();

                  myChart.setOption({
                      series : [
                          {
                              name: 'offer',
                              type: 'bar',    
                              data:v 
                          }
                      ],
                      xAxis: 
                      {
                      
                        data:l,
                    
                      },
                      title:{
                        text:title,
                        left:100,
                    },
                    legend:{
                        show:true,
                        left:200,
                        top:'top'
                    },
                      grid: {
                        show:true,
                        borderColor:"#c45455",
                        left:"20%",//grid 组件离容器左侧的距离。
                        right:"30px",
                        bottom:"40%"
                        },
                  })
                  }
              });
      }
      });
  });
}