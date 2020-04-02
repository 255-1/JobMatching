
// var testText = document.getElementById("inputText").value;


<!-- 使用测试简历进行职业匹配-->
$("#testButton").on("click", function () {
    var row2 = document.getElementById('row2');
    row2.style.display="block";

    var temp = document.createElement('span');
    temp.setAttribute('id','temp');
    temp.innerHTML = '正在查询,请稍后...';

    row2.appendChild(temp);

    $.ajax({
        dataType:"text", //数据类型
        type:"GET",
        data:{
            "inputText": document.getElementById("inputText").placeholder
        },
        success:function (data) {
            var obj =  eval('(' + data + ')');

            // 职位匹配结果显示内容
            var showMatchingResult = filterShowMatchingResult(obj)

            $("#matchingResult").html(showMatchingResult);
            $("#inputText").html(document.getElementById("inputText").placeholder);
            showElement();
            sendTable(obj);
            turnButton(obj);
            sendAbility(obj);

            var row2 = document.getElementById('row2');
            var temp = document.getElementById('temp');
            row2.removeChild(temp);

            window.result = obj.result[0];                     // 查询结果
            window.total_pages = Number(obj.total_pages);      // 总页数
            window.nowPage = obj.page;                         // 当前页数
        }
    })
})
// <!-- 对用户输入简历进行职业匹配按键-->
$("#userButton").on("click", function () {
    var row2 = document.getElementById('row2');
    row2.style.display="block";

    var temp = document.createElement('span');
    temp.setAttribute('id','temp');
    temp.innerHTML = '正在查询,请稍后...';

    row2.appendChild(temp);

    $.ajax({
        dataType:"text", //数据类型
        type:"GET",
        data:{
            "inputText": $("#inputText").val()
        },
        success:function (data) {
            var obj =  eval('(' + data + ')');
            showElement();
            sendTable(obj);
            turnButton(obj);
            sendAbility(obj);

            window.result = obj.result[0];                // 查询结果
            window.total_pages = obj.total_pages;      // 总页数
            window.nowPage = obj.page;                 // 当前页数

            var row2 = document.getElementById('row2');
            var temp = document.getElementById('temp');

            if (obj.result[0] == "非法输入") {
                row2.removeChild(temp);
                document.getElementById("row3").style.display="none";
                document.getElementById("row4").style.display="none";
                $("#matchingResult").html("<h2>没有符合您的职业, 请您输入更多履历信息</h2>");
            }
            else {
                row2.removeChild(temp);
                // 职位匹配结果显示内容
                var showMatchingResult = filterShowMatchingResult(obj)
                $("#matchingResult").html(showMatchingResult);
            }
        }
    })
})

// <!-- 跳转至第 pageId 页-->
function turnPage(pageId) {
    $.ajax({
        dataType:"text", //数据类型
        type:"GET",
        data:{
            "newPage": endPointPage(pageId),
            "jobName": window.result
        },
        success:function (data) {
            var obj =  eval('(' + data + ')');
            sendTable(obj);
            turnButton(obj);
        }
    })
};

<!--展示更多元素-->
function showElement() {
    document.getElementById("row2").style.display="block";
    document.getElementById("row3").style.display="block";
    document.getElementById("row4").style.display="block";
}

<!-- 能力分类-->
function sendAbility(obj) {
    $("#abilityTitle").html("<h2>" + obj.result[0] + " 方向就业需要以下能力</h2>");

    // 绘制词频云
    var label1WordList = obj.abilityDict.professionalWord;
    var label1CountList = obj.abilityDict.professionalCount;
    var label2WordList = obj.abilityDict.persionWord;
    var label2CountList = obj.abilityDict.persionCount;
    var label3WordList = obj.abilityDict.toolWord;
    var label3CountList = obj.abilityDict.toolCount;

    label1data = []
    for(var i=0 ;i< label1WordList.length; i++){
        label1data.push({"name": label1WordList[i],"value": label1CountList[i]});
    }
    label2data = []
    for (var i = 0; i < label2WordList.length; i++) {
        label2data.push({ "name": label2WordList[i], "value": label2CountList[i] });
    }
    label3data = []
    for (var i = 0; i < label3WordList.length; i++) {
        label3data.push({ "name": label3WordList[i], "value": label3CountList[i] });
    }

    var label1myChart = echarts.init(document.getElementById('wordCloud1'));
    var label2myChart = echarts.init(document.getElementById('wordCloud2'));
    var label3myChart = echarts.init(document.getElementById('wordCloud3'));

    label1option = {
        series: [{
            type: 'wordCloud',
            shape: 'circle',
            left: 'center',
            top: 'center',
            width: '70%',
            height: '80%',
            right: null,
            bottom: null,
            sizeRange: [12, 60],
            textStyle: {
                normal: {
                    fontFamily: 'sans-serif',
                    fontWeight: 'bold',
                },
                emphasis: {
                    shadowBlur: 10,
                    shadowColor: '#333'
                }
            },
            data: label1data
        }]
    };
    label2option = {
            series: [{
                type: 'wordCloud',
                shape: 'circle',
                width: '70%',
                height: '80%',
                right: null,
                bottom: null,
                textStyle: {
                    normal: {
                        fontFamily: 'sans-serif',
                        fontWeight: 'bold',
                    },
                    emphasis: {
                        shadowBlur: 10,
                        shadowColor: '#444'
                    }
                },
                data: label2data
            }]
        };
    label3option = {
            series: [{
                type: 'wordCloud',
                shape: 'circle',
                width: '70%',
                height: '80%',
                textStyle: {
                    normal: {
                        fontFamily: 'sans-serif',
                        fontWeight: 'bold',
                    },
                    emphasis: {
                        shadowBlur: 10,
                        shadowColor: '#555'
                    }
                },
                data: label3data
            }]
        };
    label1myChart.setOption(label1option);
    label2myChart.setOption(label2option);
    label3myChart.setOption(label3option);
}

<!-- 判断页码是否存在-->
function endPointPage(pageId) {
    var newPage = $("#" + pageId).text();
    if(pageId == "page0"){
        newPage = 1;
        alert("已经在第一页了.");
    }
    else if(pageId.substring(4,) > window.total_pages){
        newPage = pageId.substring(4,)-1;
        alert("已经到最后一页了. ");
    }
    else if(pageId == "pageInput"){
        newPage = document.getElementById('input').value;
        var str = newPage.replace(/(^\s*)|(\s*$)/g, '');            //去除空格;
        if (newPage > window.total_pages||str == '' || str == undefined || str == null){
            newPage = Number(window.nowPage);
        }
    }
    window.nowPage = newPage;
    return newPage;
}

<!--添加表格-->
function sendTable(obj) {
    document.getElementById("tableTitle").innerHTML = obj.unifyName + "共有" + obj.offers + "个offer";
    document.getElementById("tableContent").innerHTML = obj.page;

    <!--表格内容部分-->
    html = ""
    for (var line=1,l=obj.jobinfo.jobName.length; line<=l; line++){
        html += "<tr id=\"" + line + "tr\">"
        html += "<td>" + line + "</td>"
        html += "<td><a href=\"" + obj.jobinfo.jobURL[line-1] + "\" target=\"_blank" +  "\">" + obj.jobinfo.jobName[line-1] + "</a></td>"
        html += "<td>" + obj.jobinfo.company[line-1] + "</td>"
        html += "<td>" + obj.jobinfo.salary[line-1] + "</td>"
    }
    $("#tableContent").html(html);
}

// 添加表格下的页码跳转按键
function turnButton(obj) {
    var total_pages = obj.total_pages;
    var page = obj.page;                    //当前页数
    var count = 0;
    var num = 0;

    var odiv = document.getElementById("ul_t");   //获取div4
    var liList = odiv.getElementsByTagName("li");    //获取div4下的li数量
    num = liList.length;
    if (liList.length != 0){
        for(n=0;n<num+1;n++)
        {
            odiv.removeChild(odiv.childNodes[0]);
        }
    }

    <!-- 上一页按键-->
    var previousPage = page - 1;
    var p = document.getElementById("ul_t");
    var li = document.createElement("li");
    var a = document.createElement("a");
    var span = document.createElement("span");
    span.innerHTML = "&laquo;";
    a.setAttribute('id', 'previous');
    a.setAttribute('href', '#row4');
    a.setAttribute('aria-label', 'Previous');
    a.setAttribute('onclick', "turnPage(\"" + "page" + previousPage + "\")");
    li.appendChild(a);
    a.appendChild(span);
    p.append(li);
    span.setAttribute('aria-hidden', "true");

    <!-- 页码按键-->
    for(i=1;i<=total_pages;i++)
    {
        var p=document.getElementById("ul_t");
        var li=document.createElement("li");
        var a=document.createElement("a");

        if (total_pages > 10){
            if (i > 10 && page < 6){           // 总页>10 且 未翻页到第6页 ····· 10页以上显示...
                a.innerHTML="...";
                li.appendChild(a);
                p.appendChild(li);
                p.append(li);
                break;
            }else if (page >= 6 && page <= total_pages-6){      // 翻到大于等于第6页， 并且剩余页数大于6
                if (i == 1){
                    a.innerHTML="...";
                    li.appendChild(a);
                    p.appendChild(li);
                    p.append(li);
                    continue;
                }
                else if (page - i >3 || i > page+6){
                    continue;
                }
                if (i == page+6 && i != total_pages){
                    a.innerHTML="...";
                    li.appendChild(a);
                    p.appendChild(li);
                    p.append(li);
                    continue;
                }
            }else if (page > total_pages-6){
                if (i == 1){
                    a.innerHTML="...";
                    li.appendChild(a);
                    p.appendChild(li);
                    p.append(li);
                    continue;
                }
                else if (total_pages-9 > i){
                    continue;
                }
            }
        }
        a.innerHTML=i;
        a.setAttribute('id', 'page'+i);
        a.setAttribute('href', '#row4');
        a.setAttribute('value', i);
        a.setAttribute('onclick', "turnPage(" + "\"page" + i + "\")");
        li.appendChild(a);
        if(i==page){
            li.setAttribute("class","active")
            p.appendChild(li);
        }
        else{
            p.append(li);
        }
        count +=1;
    }

    <!-- 下一页按键-->
    var nextPage = page + 1;
    var p = document.getElementById("ul_t");
    var li = document.createElement("li");
    var a = document.createElement("a");
    var span = document.createElement("span");
    span.innerHTML = "&raquo;";
    a.setAttribute('id', 'next');
    a.setAttribute('href', '#row4');
    a.setAttribute('aria-label', 'Next');
    a.setAttribute('onclick', "turnPage(\"" + "page" + nextPage + "\")");
    li.appendChild(a);
    a.appendChild(span);
    p.append(li);
    span.setAttribute('aria-hidden', "true");


    <!-- 输入跳转换页 输入框和按键-->
    var nextPage = page + 1;
    var p = document.getElementById("ul_t");
    var form = document.createElement("form");
    var input = document.createElement("input");
    var button = document.createElement("button");
    button.innerHTML = "跳转至该页";
    form.setAttribute("class", "navbar-form navbar-left");
    form.setAttribute("id", "form");
    form.setAttribute("style", "display: inline-block;transform: translateY(-20%)");
    input.setAttribute("type", "number");
    input.setAttribute("min", "1");
    input.setAttribute("max", obj.total_pages);
    input.setAttribute("class", "form-control");
    input.setAttribute("aria-describedby", "basic-addon1");
    input.setAttribute("id", "input");
    input.setAttribute("value", "");
    button.setAttribute("class", "btn btn-default navbar-btn");
    button.setAttribute("onclick", "turnPage(\"" + "pageInput" + "\")");

    p.appendChild(form);
    form.appendChild(input);
    form.appendChild(button);
}

<!--界面保存为PDF-->
var downPdf = document.getElementById("renderPdf");
downPdf.onclick = function() {
    html2canvas(document.body, {
        onrendered:function(canvas) {

            //返回图片URL，参数：图片格式和清晰度(0-1)
            var pageData = canvas.toDataURL('image/jpeg', 1.0);

            //方向默认竖直，尺寸ponits，格式a4【595.28,841.89]
            var pdf = new jsPDF('', 'pt', 'a4');

            //需要dataUrl格式
            pdf.addImage(pageData, 'JPEG', 0, 0, 595.28, 592.28/canvas.width * canvas.height );

            pdf.save('专属职业匹配报表.pdf');
        }
    })
}


// 职业匹配结果进行简单过滤
function filterShowMatchingResult(obj) {
    var showMatchingResult;
    if (obj.result.length/2<3){
        var showText = '<h2>AI 预测您的职业倾向如下:</h2>';

        for (var i=0;i<obj.result.length/2;i++){
            showText = showText + '[ ' + obj.result[2*i] + ' ] 方向系统匹配度:  ' + obj.result[2*i+1] + '%<br/>';
        }
        showText = showText + '<h3>Tips: 输入详细简历获得更多职业方向......</h3>';
        showMatchingResult = showText;
    }
    else{
        var showText = '<h2>AI 预测您的职业倾向如下:</h2>';
        for (var i=0;i<obj.result.length/2;i++)
        {
            if (i < 5){
                showText = showText + '[ ' + obj.result[2*i] + ' ] 方向系统匹配度:  ' + obj.result[2*i+1] + '%<br/>';
            }
            else{
                var otherJobSum = 0;
                for(var j=5;i<obj.result.length/2;i++)
                {
                    otherJobSum = otherJobSum + obj.result[2*i+1];
                }
                showText = showText + ' 其他 方向系统匹配度:  ' + otherJobSum + '%<br/>';
            }
        }
        showMatchingResult = showText;
    }

    return showMatchingResult;
}