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
        },
        yAxis: {

            type: 'value',
            name:"月薪(万/月)"

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
            left:300,
            top:'top'
        },

        // Declare several bar series, each will be mapped
        // to a column of dataset.source by default.
        series: {
            name:"月薪",
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