package filter;

import bean.JobBean;
import save.ConnectMySQL;
import save.DBUtils;
import save.JobBeanDBUtils;
import save.JobBeanLocalUtils;
import task.GlobalConfiguration;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class JobBeanUnify {
    private static Connection connJob = ConnectMySQL.getConnectionJob();

    /**
     * @Author: PowerZZJ
     * @Description:数据库中加入统一命名列
     * 此为后续增加功能，不与其余类有联系，添加功能的相关函数都在这个类中
     */
    public static void addUnifyName() {
        List<JobBean> jobBeanList;
        //对存入数据库的增量职位
        jobBeanList = JobBeanDBUtils.selectJobBeanList(GlobalConfiguration.getJobinfoTablename());
        //获取数据库提取出的名字的对应关键字文件
        JobBeanUnify.getReference(jobBeanList);
        //数据库添加没有的关键字职位名
        JobBeanUnify.addUnifyNameIntoDB(JobBeanUnify.loadReference(GlobalConfiguration.getReferenceSaveName()));
    }

    /**
     * @Author: PowerZZJ
     * @Description:对比url和jobBean，统一职位名
     */
    public static void addUnifyNameIntoDB(List<String> referenceList) {
        System.out.println("开始修改数据库的unifyName");
        String updateCommand = "update " + GlobalConfiguration.getJobinfoTablename() +
                " set unifyName='%s' where jobName='%s' and company='%s' and date='%s';";
        for (String line : referenceList) {
            String[] datas = line.split(":");
            String jobName = datas[0];
            String company = datas[1];
            String date = datas[2];
            String unifyName = datas[3];
            String command = String.format(updateCommand, unifyName, jobName, company, date);
            DBUtils.executeUpdate(connJob, command);
        }
        System.out.println("修改完成");
    }

    /**
     * @Author: PowerZZJ
     * @Description:对比jobUrl文本内容，找到对应的关键字
     */
    public static void getReference(List<JobBean> jobBeanList) {
        System.out.println("开始职位名参考关系的文件保存");
        //读取joburl的url和关键字，进行对应，时间复杂度n^2
        List<String> jobUrlList = JobBeanLocalUtils.loadJobUrlList(GlobalConfiguration.getJoburlSaveName());

        //记录对应关系
        StringBuffer sb = new StringBuffer();
        for (JobBean jobBean : jobBeanList) {
            String url1 = jobBean.getJobURL();
            for (String line : jobUrlList) {
                String unifyName = line.split(",")[0];
                String url2 = line.split(",")[1];
                if (url1.equals(url2)) {
                    sb.append(jobBean.getJobName()).append(":").
                            append(jobBean.getCompany()).append(":").
                            append(jobBean.getDate()).append(":").
                            append(unifyName).append("\n");
                    break;
                }
            }
        }
        saveReference(new String(sb), GlobalConfiguration.getReferenceSaveName());
        System.out.println("结束职位名参考关系的文件保存");
    }

    /**
     * @Author: PowerZZJ
     * @Description:读取关键字职位名对应关系的文件，
     */
    public static List<String> loadReference(String fileName) {
        List<String> referenceList = new ArrayList<>();
        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(fileName))) {
            String str;
            while ((str = br.readLine()) != null) {
                referenceList.add(str);
            }
            System.out.println("本地文件一共读取" + referenceList.size() + "行reference数据");
        } catch (Exception e) {
            System.out.println("读取本地备份文件失败");
            e.printStackTrace();
        }
        return referenceList;
    }

    /**
     * @Author: PowerZZJ
     * @Description:保存关键字和职位名的对应关系,
     * 保存格式 jobName:company:date:unifyName\n保存，为数据库插入做准备
     */
    public static void saveReference(String str, String fileName) {
        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(fileName, true))) {
            bw.write(str);
            bw.flush();
        } catch (IOException e) {
            System.out.println("保存reference失败");
            e.printStackTrace();
        }
    }

}
