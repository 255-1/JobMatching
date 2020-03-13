/*
    当前一共三个画图函数

    draw_bar()
    draw_line()
    draw_pie()

    labels:后端应用传来的标签值 Arrary
    values:后端应用传来的统计值 Arrary
    id：作用的目标div id值 str
    title：图表的标题 str
*/


function draw_bar(labels,values,id,title)
{
    var dom = document.getElementById(id);
    var myChart = echarts.init(dom,'roma');

    option = {
        legend: {},
        tooltip: {},
        xAxis: {
            type: 'category',
            data:labels,
            axisLabel: {
                interval: 0,
                // rotate: 30
                formatter : function(value){
                    var len=value.length;//单个标签的字符长度
                    var max_num=4;
                    var row_num=Math.ceil(len/max_num);

                    var last_str="";

                    if(row_num > 1)
                    {
                        for (var i=0;i<row_num;i++)
                        {
                            var temp="";
                            var start=i*max_num;
                            var end=start+max_num;

                            if ( i== row_num -1)
                            {
                                temp=value.substring(start,len);
                            }
                            else
                            {
                                temp=value.substring(start,end)+"\n";
                            }

                            last_str += temp;


                        }
                    }
                    else
                    {
                        last_str=value;
                    }

                    return last_str;
                    
                  }
            },
               
        },
        grid: 
        {
            show:true,
            borderColor:"#c45455",
            left:"20%",//grid 组件离容器左侧的距离。
            right:"30px",
            bottom:"40%"
        },
        yAxis: {
            
            type: 'value',
            name:"Offer个数"
            
        },
        brush: {
            toolbox: ['rect', 'polygon', 'lineX', 'lineY', 'keep', 'clear'],
            xAxisIndex: 0
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

        // Declare several bar series, each will be mapped
        // to a column of dataset.source by default.
        series: {
            name:"offer",
            type: 'bar',
            data:values,

        },
        toolbox:{
            feature: {
                magicType:
                    {
                    type: ['stack', 'tiled']
                }
            }
        }
    };


    // 使用刚指定的配置项和数据显示图表。
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }

    return myChart;
}


function draw_line(labels,values,id,title)
{

    var dom = document.getElementById(id);
    var myChart = echarts.init(dom,'roma');

    option = {
        legend: {},
        tooltip: {},
        xAxis: {
            type: 'category',
            data:labels,
            axisLabel: {
                interval: 0,
                // rotate: 30
                
            },
               
        },
        grid: 
        {
            show:true,
            borderColor:"#c45455",
            left:"20%",//grid 组件离容器左侧的距离。
            right:"30px",
            bottom:"40%"
        },
        yAxis: {
            
            type: 'value',
            name:"Offer个数"
            
        },
        brush: {
            toolbox: ['rect', 'polygon', 'lineX', 'lineY', 'keep', 'clear'],
            xAxisIndex: 0
        },
        title:{
            
            text:title,
            left:100
            
        },
        legend:{
            show:true,
            left:200,
            top:'top'
        },

        // Declare several bar series, each will be mapped
        // to a column of dataset.source by default.
        series: {
            name:"offer",
            type: 'line',
            smooth:true,
            data:values
        },
        toolbox:{
            feature: {
                magicType:
                    {
                    type: ['stack', 'tiled']
                }
            }
        }
    };



    // 使用刚指定的配置项和数据显示图表。
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }

    return myChart;


}


function draw_pie(labels,values,id,title)
{

    var dom = document.getElementById(id);
    var myChart = echarts.init(dom,'roma');

    option = {
        legend: { show:false },
        tooltip: {},
        
        brush: {
            toolbox: ['rect', 'polygon', 'lineX', 'lineY', 'keep', 'clear'],
            xAxisIndex: 0
        },
        title:{
            text:title,
            right:50,
            top:300
        },

        // Declare several bar series, each will be mapped
        // to a column of dataset.source by default.
        series: {
            name:"offer数量",
            type: 'pie',
            smooth:true,
            data:[
                {value:values[0],name:labels[0]},
                {value:values[1],name:labels[1]},
                {value:values[2],name:labels[2]},
                {value:values[3],name:labels[3]},
                {value:values[4],name:labels[4]},
                {value:values[5],name:labels[5]},
                {value:values[6],name:labels[6]},
                {value:values[7],name:labels[7]},
                {value:values[8],name:labels[8]}
            ],
             hoverAnimation : true,
            radius : '65%'
        },
        toolbox:{
            feature: {
                magicType:
                    {
                    type: ['stack', 'tiled']
                }
            }
        }
    };



    // 使用刚指定的配置项和数据显示图表。
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }

    return myChart;


}

