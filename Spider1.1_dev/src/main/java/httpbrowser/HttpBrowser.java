package httpbrowser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class HttpBrowser {

    /**
     * @Author: PowerZZJ
     * @param: url 网址
     * @return: 字符串网页
     * @Description:通过本机ip地址获得网站html
     */
    public static String getHtml(String url) {
        if (url == null || url.length() == 0) return "";
        //新建get请求
        HttpGet httpGet = new HttpGet(url);
        //添加请求头配置
        addHeaders(httpGet);
        addConfigs(httpGet);
        //接受响应
        return getValidHttpResponse(httpGet);
    }

    /**
     * @Author: PowerZZJ
     * @param: url 网址
     * @return: 字符串网页
     * @Description:通过代理ip地址获得网站html
     */
    public static String getHtml(String url, HttpHost proxy) {
        if (url == null || url.length() == 0) return "";
        //新建get请求,新建代理
        HttpGet httpGet = new HttpGet(url);
        //添加请求头配置
        addHeaders(httpGet);
        addConfigs(httpGet, proxy);
        //接受响应
        return getValidHttpResponse(httpGet);
    }

    /**
     * @Author: PowerZZJ
     * @param: url 网址
     * @return: 字符串网页
     * @Description:通过代理ip地址获得网站html
     */
    public static String getHtml(String url, String ipAddress, String ipPort) {
        if (url == null || url.length() == 0) return "";
        if (ipAddress == null || ipAddress.length() == 0) return "";
        if (ipPort == null || ipPort.length() == 0) return "";

        //新建get请求,新建代理
        HttpHost proxy = getHttpHost(ipAddress, ipPort);
        return getHtml(url, proxy);
    }

//-------------------------------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @param: 请求
     * @return: 连接成功的网页信息
     * @Description:接受200响应码的内容，并转成utf8格式
     */
    public static String getValidHttpResponse(HttpGet httpGet) {
        //创建客户端和请求体
        try (CloseableHttpClient httpClient = createHttpClient();
             CloseableHttpResponse httpResponse = httpClient.execute(httpGet);) {
            //如果响应码是200，
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return transHttpEntityToUtf8(httpResponse.getEntity());
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * @Author: PowerZZJ
     * @return: HttpClient对象，
     * @Description: 创建HttpClient对象，
     */
    public static CloseableHttpClient createHttpClient() {
        //配置相关socket超时配置，不适用default创建，会遇到read0阻塞问题
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(3000).build();
        return HttpClients.custom().setDefaultSocketConfig(socketConfig).build();
    }

    /**
     * @Author: PowerZZJ
     * @Description:给httpget对象添加基本头文件，没有使用代理
     */
    public static void addHeaders(HttpGet httpGet) {
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Connection", "closer");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
    }

    /**
     * @Author: PowerZZJ
     * @Description:给httpget对象添加超时配置文件，没有使用代理
     */
    public static void addConfigs(HttpGet httpGet) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).
                setSocketTimeout(3000).build();
        httpGet.setConfig(config);
    }

    /**
     * @Author: PowerZZJ
     * @Description:给httpget对象添加超时配置文件，使用代理
     */
    public static void addConfigs(HttpGet httpGet, HttpHost proxy) {
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(3000).
                setSocketTimeout(3000).setConnectionRequestTimeout(3000).build();
        httpGet.setConfig(config);
    }

    /**
     * @Author: PowerZZJ
     * @param: ip地址和端口
     * @return: 代理类
     * @Description:更具字符串的ip地址和端口返回代理的HttpHost对象
     */
    public static HttpHost getHttpHost(String ipAddress, String ipPort) {
        return new HttpHost(ipAddress, Integer.parseInt(ipPort));
    }

    /**
     * @Author: PowerZZJ
     * @param: 网页实体
     * @return: utf8字符集的网页内容
     * @Description：接受网页实体，查找字符集，并转为utf8字符集
     */
    public static String transHttpEntityToUtf8(HttpEntity httpEntity) {
        try {
            byte[] bytes = EntityUtils.toByteArray(httpEntity);
            //解析网页成源字符集
            String entity = new String(bytes, getCharset(bytes));
            //转换为统一的utf-8
            return new String(entity.getBytes(), "utf-8");
        } catch (IOException e) {

        }
        return "";
    }

    /**
     * @Author: PowerZZJ
     * @param: bytes EntityUtils获取网页的bytes
     * @return: 字符集
     * @Description:获取字符集，适用于51job，猎聘和西刺代理网
     */
    private static String getCharset(byte[] bytes) {
        //以下字符集解析与转换的代码，不要动.
        String html = new String(bytes);
        Document document = Jsoup.parse(html);
        //字符集信息提取，51job和猎聘
        String[] charset = document.select("meta[http-equiv=Content-Type]")
                .attr("content").split("=");
        //如果不在51job网页上，没有字符集返回utf8
        if (charset.length != 2) return "utf8";
        return charset[1];
    }

}
