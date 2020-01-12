package save;

import bean.JobBean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class JobBeanLocalUtils {
    //JobBean属性数量
    private static final int JOBBEAN_ATTRIBUTE_NUMBER = 13;

    /**
     * @Author: PowerZZJ
     * @param: fileName 文件名
     * jobBean 职位
     * @Description: 保存单个jobBean
     */
    public static void saveJobBean(JobBean jobBean, String fileName) {
        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(fileName, true))) {
            String row = jobBean.saveString();
            bw.write(row);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            System.out.println("保存JobBean失败");
            e.printStackTrace();
        }
    }

    /**
     * @Author: PowerZZJ
     * @param: fileName 文件名
     * jobBeanList 职位列表
     * @Description: 保存职位列表
     */
    public static void saveJobBeanList(List<JobBean> jobBeanList, String fileName) {
        for (JobBean jobBean : jobBeanList) {
            saveJobBean(jobBean, fileName);
        }
    }

    /**
     * @Author: PowerZZJ
     * @param: fileName 文件名
     * @return: 职位列表
     * @Description:从本地读取职位列表
     */
    public static List<JobBean> loadJobBeanList(String fileName) {
        List<JobBean> jobBeanList = new ArrayList<>();
        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(fileName))) {
            String str = null;
            while ((str = br.readLine()) != null) {
                JobBean jobBean = getJobBeanFromLine(str);
                if (null != jobBean) {
                    jobBeanList.add(jobBean);
                }
            }
            System.out.println("本地文件一共读取" + jobBeanList.size() + "行jobinfo数据");
        } catch (Exception e) {
            System.out.println("读取本地备份文件失败");
            e.printStackTrace();
        }
        return jobBeanList;
    }

    /**
     * @Author: PowerZZJ
     * @param: fileName 文件名
     * @return: 职位列表
     * @Description:从本地读取职位url列表
     */
    public static List<String> loadJobUrlList(String fileName) {
        List<String> jobUrlList = new ArrayList<>();
        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(fileName))) {
            String str = null;
            while ((str = br.readLine()) != null) {
                jobUrlList.add(str);
            }
            System.out.println("本地文件一共读取" + jobUrlList.size() + "行joburl数据");
        } catch (Exception e) {
            System.out.println("读取本地备份文件失败");
            e.printStackTrace();
        }
        return jobUrlList;
    }

    /**
     * @Author: PowerZZJ
     * @param: 职位url列表
     * fileName 文件名
     * @Description:职位url列表保存到本地
     */
    public static void saveJobUrlList(List<String> jobUrlList, String keyWord, String fileName) {
        for (String jobUrl : jobUrlList) {
            saveJobUrl(jobUrl, keyWord, fileName);
        }

    }

    /*@Author: PowerZZJ
     *@param: 职位url
     * fileName 文件名
     *@Description:职位url保存到本地
     */
    public static void saveJobUrl(String jobUrl, String keyWord, String fileName) {
        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(fileName, true))) {
            bw.write(keyWord + "," + jobUrl);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Author: PowerZZJ
     * @param: 一行字符串
     * @return: 职位
     * @Description:从字符串解析出职位
     */
    public static JobBean getJobBeanFromLine(String line) {
        String[] dataList = line.split("     ");
        //分解后，不符合长度的职位不读取
        if (dataList.length != JOBBEAN_ATTRIBUTE_NUMBER) {
            return null;
        }
        JobBean jobBean = new JobBean();
        jobBean.setJobName(dataList[0]);
        jobBean.setCompany(dataList[1]);
        jobBean.setAddress(dataList[2]);
        jobBean.setSalary(dataList[3]);
        jobBean.setDate(dataList[4]);
        jobBean.setExp(dataList[5]);
        jobBean.setEdu(dataList[6]);
        jobBean.setOfferNumber(dataList[7]);
        jobBean.setJobInfo(dataList[8]);
        jobBean.setCompanyType(dataList[9]);
        jobBean.setStaffNumber(dataList[10]);
        jobBean.setCompanyOrientation(dataList[11]);
        jobBean.setJobURL(dataList[12]);

        return jobBean;
    }

}
