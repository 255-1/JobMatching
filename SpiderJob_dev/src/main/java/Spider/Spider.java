package Spider;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @description: 父类
 * 和指定的URL建立链接，获取并解析网页
 * @author: PowerZZJ
 * @date: 2019/11/30
 */
public class Spider {

    private Document document;
    private String strURL;

    public Spider() {
    }

    public Spider(String strURL) {
        this.strURL = strURL;
    }

    /**
     * 获取51job和猎聘网字符集
     *
     * @param bytes HttpEntity字节流
     * @return 字符集
     * @author PowerZZJ
     */
    private String AcquireCharset(byte[] bytes) {
        //以下字符集解析与转换的代码，不要动.
        String get_Charset_String = new String(bytes);
        Document get_Charset_Document = Jsoup.parse(get_Charset_String);
        //字符集信息提取，51job和猎聘
        return get_Charset_Document.select("meta[http-equiv=Content-Type]")
                .attr("content").split("=")[1];
    }

    /**
     * @Author: PowerZZJ
     * @param:strURL URL地址
     * @return: 解析后的网页
     * @Description: 与strURL建立链接，统一字符集为utf-8,
     */
    public Document AcquireDocument(String strURL) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(strURL);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(4000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(4000).build();
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //以下字符集解析与转换的代码，不要动，
                //不要尝试使用EntityUtils.toString()方法
                byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
                String Ori_Entity = new String(bytes, AcquireCharset(bytes));
                //转换为统一的utf-8
                String entity = new String(Ori_Entity.getBytes(), "utf-8");
                return Jsoup.parse(entity);
            }
        } catch (Exception e) {
            System.out.println("网页连接失败:" + strURL);
        }
        return null;
    }

    public void SpiderAll(String strURL) {
        this.strURL = strURL;
        document = AcquireDocument(strURL);
    }

    public Document getDocument() {
        return document;
    }


    public String getStrURL() {
        return strURL;
    }

    public void setStrURL(String strURL) {
        this.strURL = strURL;
    }
}
