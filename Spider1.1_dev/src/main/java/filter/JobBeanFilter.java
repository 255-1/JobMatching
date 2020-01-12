package filter;

import bean.JobBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class JobBeanFilter {
    private static List<JobBean> removeList = new ArrayList<>();

    private static List<String> eduList = Arrays.asList("本科", "硕士", "大专", "高中", "博士", "没有要求");
    private static List<String> companyTypeList = Arrays.asList("国企", "民营公司", "合资", "外资（欧美）",
            "上市公司", "事业单位", "外资（非欧美）", "创业公司", "非营利组织");
    private static List<String> staffNumberList = Arrays.asList("500-1000人", "150-500人", "50-150人",
            "1000-5000人", "5000-10000人", "少于50人",
            "10000人以上");
    private static List<String> salaryList = Arrays.asList("万/月", "千/月", "万/年");
    private static List<String> comapnyOrientationList = Arrays.asList("计算机", "电子商务", "互联网", "教育",
            "银行", "网络游戏", "电子技术", "通信", "金融", "医疗");


    /**
     * @Author: PowerZZJ
     * @param: 职位信息列表
     * @Description:清洗过程
     */
    public static void filter(List<JobBean> jobBeanList) {
        for (JobBean jobBean : jobBeanList) {
            if (jobBeanIsValid(jobBean) == false) {
                removeList.add(jobBean);
                continue;
            }
            if (jobNameIsValid(jobBean) == false) {
                removeList.add(jobBean);
                continue;
            }

            if (expIsValid(jobBean) == false) {
                removeList.add(jobBean);
                continue;
            }
            if (eduIsValid(jobBean) == false) {
                removeList.add(jobBean);
                continue;
            }
            if (offerNumberIsValid(jobBean) == false) {
                removeList.add(jobBean);
                continue;
            }
            if (companyTypeIsValid(jobBean) == false) {
                removeList.add(jobBean);
                continue;
            }
            if (staffNumberIsValid(jobBean) == false) {
                removeList.add(jobBean);
                continue;
            }
            //需要转换的放到最后转换
            if (dateIsValid(jobBean)) {
                transDate(jobBean);
            } else {
                System.out.println(jobBean.getDate());
                removeList.add(jobBean);
                continue;
            }
            if (salaryIsValid(jobBean)) {
                transSalary(jobBean);
            } else {
                removeList.add(jobBean);
                continue;
            }
            if (comapnyOrientationIsValid(jobBean)) {
                transCompanyOrientation(jobBean);
            } else {
                removeList.add(jobBean);
                continue;
            }

        }

        jobBeanList.removeAll(removeList);
        System.out.println("清洗后为" + jobBeanList.size() + "条数据");

    }
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------


    /**
     * @Author: PowerZZJ
     * @Description: 判断jobBean是否含有空值
     */
    public static boolean jobBeanIsValid(JobBean jobBean) {
        return !(jobBean.getJobName().isEmpty() ||
                jobBean.getCompany().isEmpty() ||
                jobBean.getAddress().isEmpty() ||
                jobBean.getSalary().isEmpty() ||
                jobBean.getDate().isEmpty() ||
                jobBean.getExp().isEmpty() ||
                jobBean.getEdu().isEmpty() ||
                jobBean.getOfferNumber().isEmpty() ||
                jobBean.getCompanyType().isEmpty() ||
                jobBean.getStaffNumber().isEmpty() ||
                jobBean.getCompanyOrientation().isEmpty() ||
                jobBean.getJobURL().isEmpty());
    }


    /**
     * @Author: PowerZZJ
     * @Description: 判断工资类型
     */
    public static boolean salaryIsValid(JobBean jobBean) {
        int index = jobBean.getSalary().indexOf("/");
        if (index == -1) return false;
        //取出工资单位
        String salaryType = jobBean.getSalary().substring(index - 1);
        //不在范围内的去除
        if (salaryList.contains(salaryType)) {
            //不是区间的去除
            String[] salary = jobBean.getSalary().substring(0, index - 1).split("-");
            if (salary.length != 2) return false;
            return true;
        }
        return false;
    }

    /**
     * @Author: PowerZZJ
     * @Description:转化工资单位
     */
    public static void transSalary(JobBean jobBean) {
        int index = jobBean.getSalary().indexOf("/");
        String salaryType = jobBean.getSalary().substring(index - 1);
        String[] salary = jobBean.getSalary().substring(0, index - 1).split("-");

        if ("万/月".equals(salaryType)) {
            jobBean.setSalary(salary[0] + "-" + salary[1]);
        }
        if ("千/月".equals(salaryType)) {
            //保留小数点后1位
            Double transLow = Double.parseDouble(salary[0]);
            transLow = (double) (Math.round(transLow)) / 10;
            Double transHigh = Double.parseDouble(salary[1]);
            transHigh = (double) (Math.round(transHigh)) / 10;
            jobBean.setSalary(transLow + "-" + transHigh);
        }
        if ("万/年".equals(salaryType)) {
            Double transLow = Double.parseDouble(salary[0]);
            transLow = (double) (Math.round(transLow * 10 / 12)) / 10;
            Double transHigh = Double.parseDouble(salary[1]);
            transHigh = (double) (Math.round(transHigh * 10 / 12)) / 10;
            jobBean.setSalary(transLow + "-" + transHigh);
        }
    }

    /**
     * @Author: PowerZZJ
     * @Description: 保留三天内的，以爬取当天日期为标准,
     * 月初头三天不计算上月月末的日期
     */
    public static boolean dateIsValid(JobBean jobBean) {
        if (!jobBean.getDate().contains("发布")) return false;
        //获取当天的日期，格式为yyyy-MM-dd
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String today = year + "-" + month + "-" + day;
        //获取jobBean的发布日期，转为格式为yyyy-MM-dd,yyyy来自Calendar
        String date = jobBean.getDate();
        date = year + "-" + date.substring(0, date.indexOf("发布"));
        //格式化为Date，进行比较
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = df.parse(date);
            Date d2 = df.parse(today);
            if (d1.getMonth() != d2.getMonth()) return false;
            int margin = d2.getDay()-d1.getDay();
            if (margin >= -6 && margin <= 6) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @Author: PowerZZJ
     * @Description:转换发布日期为yyyy-MM-dd
     */
    public static void transDate(JobBean jobBean) {
        //获取jobBean的发布日期，格式为 MM-dd
        String date = jobBean.getDate();
        int index = jobBean.getDate().indexOf("发布");
        try {
            date = date.substring(0, index);
        } catch (Exception e) {
            System.out.println(date);
        }

        //获取当天的日期，格式为 MM-dd
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        jobBean.setDate(year + "-" + date);
    }

    /**
     * @Author: PowerZZJ
     * @Description:转换公司目标为第一目标
     */
    public static void transCompanyOrientation(JobBean jobBean) {
        String co = jobBean.getCompanyOrientation();
        co = co.split("/")[0];
        jobBean.setCompanyOrientation(co);
    }


    /**
     * @Author: PowerZZJ
     * @Description:去除jobName包含"\"转义符或者英文":"，后续数据库操作会出错
     */
    public static boolean jobNameIsValid(JobBean jobBean) {
        return jobBean.getJobName().contains("\\") == false &&
                jobBean.getJobName().contains(":") == false;
    }

    /**
     * @Author: PowerZZJ
     * @Description: 判断公司人数是否符合
     */
    public static boolean staffNumberIsValid(JobBean jobBean) {
        return staffNumberList.contains(jobBean.getStaffNumber());
    }

    /**
     * @Author: PowerZZJ
     * @Description: 判断公司类型是否符合
     */
    public static boolean companyTypeIsValid(JobBean jobBean) {
        return companyTypeList.contains(jobBean.getCompanyType());
    }

    /**
     * @Author: PowerZZJ
     * @Description: 判断招收数量是否符合
     */
    public static boolean offerNumberIsValid(JobBean jobBean) {
        return jobBean.getOfferNumber().contains("招");
    }

    /**
     * @Author: PowerZZJ
     * @Description: 判断教育经历是否符合
     */
    public static boolean eduIsValid(JobBean jobBean) {
        return eduList.contains(jobBean.getEdu());
    }

    /**
     * @Author: PowerZZJ
     * @Description: 判断经验是否符合
     */
    public static boolean expIsValid(JobBean jobBean) {
        return jobBean.getExp().contains("经验");
    }

    /**
     * @Author: PowerZZJ
     * @Description: 判断公司方向是否符合, 取第一个方向
     */
    public static boolean comapnyOrientationIsValid(JobBean jobBean) {
        String co = jobBean.getCompanyOrientation();
        co = co.split("/")[0];
        for (String s : comapnyOrientationList) {
            if (co.contains(s)) return true;
        }
        return false;
    }

}
