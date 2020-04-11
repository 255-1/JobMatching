


function draw_bar_index(labels,values,id,title)
{
    var dom = document.getElementById(id);
    var myChart = echarts.init(dom);

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
        yAxis: {

            type: 'value',
            name:"月薪(万/月)",
            min:1

        },
        brush: {
            toolbox: [  'lineX',  'keep', 'clear'],
            xAxisIndex: 0
        },
        title:{

            text:title,



        },
        legend:{
            show:true,

        },

        // Declare several bar series, each will be mapped
        // to a column of dataset.source by default.
        series: {
            name:"月薪",
            type: 'bar',
            data:values,
            markLine : {
                symbol : 'none',
                itemStyle : {
                        normal :
                    {
                        color:'#0f0307'
                    }
                },
                data : [{type : 'average', name: '平均值'}]
             },
            itemStyle: {
                color: '#aa2116'
            },

        },

    };


    // 使用刚指定的配置项和数据显示图表。
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }

    return myChart;
}

//给表格加上箭头
function arrow_sal(data,diff,tbody_id,tr_pre_id)
{
    for (var i=1;i<=data[0].length;i++)
        {
            var tbody = document.getElementById(tbody_id);
            var tr = document.createElement("tr");

            tr.id = tr_pre_id+i+"_tr";
            tbody.appendChild(tr);

            for(var j =0;j<4;j++)
            {
                // var tr = document.getElementById(tr_pre_id+i+"_tr");
                var td = document.createElement("td");

                if(j == 0)
                {
                    var content = i;
                }
                else if(j ==2 )
                {
                    var arrow;
                    //判断升降
                    if(diff[i-1][0]>0)
                    {
                        arrow = "<i class='fa fa-arrow-up' style='color:#B0171F'></i>";
                    }
                    else if(diff[i-1][0]<0)
                    {
                        arrow = "<i class='fa fa-arrow-down' style='color:#90EE90'></i>";
                    }
                    else
                    {
                        arrow="";
                    }
                    var content = data[j-1][i-1]+arrow;
                }
                else if(j ==3 )
                {
                    var arrow;
                    //判断升降
                    if(diff[i-1][1]>0)
                    {
                        arrow = "<i class='fa fa-arrow-up' style='color:#B0171F'></i>";
                    }
                    else if(diff[i-1][1]<0)
                    {
                        arrow = "<i class='fa fa-arrow-down' style='color:#90EE90'></i>";
                    }
                    else
                    {
                        arrow="";
                    }
                    var content = data[j-1][i-1]+arrow;
                }
                else
                {
                    var content = data[j-1][i-1];
                }

                td.innerHTML=content;
                tr.appendChild(td);

            }

    }
}



function arrow_offer(data,diff,tbody_id,tr_pre_id)
{
    for (var i=1;i<=data[0].length;i++)
        {
            var tbody = document.getElementById(tbody_id);
            var tr = document.createElement("tr");

            tr.id = tr_pre_id+i+"_tr";
            tbody.appendChild(tr);

            for(var j =0;j<4;j++)
            {
                var td = document.createElement("td");

                if(j == 0)
                {
                    var content = i;
                }
                else if(j ==3 )
                {
                    var arrow;
                    //判断升降
                    if(diff[i-1]>0)
                    {
                        arrow = "<i class='fa fa-arrow-up' style='color:#B0171F'></i>";
                    }
                    else if(diff[i-1]<0)
                    {
                        arrow = "<i class='fa fa-arrow-down' style='color:#90EE90'></i>";
                    }
                    else
                    {
                        arrow="";
                    }
                    var content = data[j-1][i-1]+arrow;
                }
                else
                {
                    var content = data[j-1][i-1];
                }

                td.innerHTML=content;
                tr.appendChild(td);

            }

    }
}