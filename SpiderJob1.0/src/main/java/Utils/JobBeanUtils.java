package Utils;

import java.io.*;
import java.util.*;

import Base.JobBean;

/**实现
 * 1。将JobBean容器存入本地
 * 2.从本地文件读入文件为JobBean容器
 * @author PowerZZJ
 *
 */
public class JobBeanUtils {

	/**追加JobBean到本地功能实现
	 * @param job
	 */
	private static void saveJobBean(JobBean job, String fileName) {
		try(BufferedWriter bw =
				new BufferedWriter(
						new FileWriter(fileName,true))){
			String jobInfo = job.saveString();
			bw.write(jobInfo);
			bw.newLine();
			bw.flush();
		}catch(Exception e) {
			System.out.println("保存JobBean失败");
			e.printStackTrace();
		}
	}
	
	/**保存JobBean容器到本地功能实现
	 * @param jobBeanList JobBean容器
	 */
	public static void saveJobBeanList(List<JobBean> jobBeanList, String fileName) {
		System.out.println("正在备份容器到本地");
		for(JobBean jobBean : jobBeanList) {
				saveJobBean(jobBean, fileName);
		}
		System.out.println("备份完成,一共"+jobBeanList.size()+"条信息");
	}
	
	/**从本地文件读入文件为JobBean容器(有筛选)
	 * @return jobBean容器
	 */
	public static List<JobBean> LoadJobBeanList(String fileName){
		List<JobBean> jobBeanList = new ArrayList<>();
		try(BufferedReader br = 
				new BufferedReader(
						new FileReader(fileName))){
			String str = null;
			while((str=br.readLine())!=null) {
				try {
					String[] datas = str.split("    "); 
					String jobName = datas[0];
					String company = datas[1];
					String address = datas[2];
					String salary = datas[3];
					String date = datas[4];
					String exp = datas[5];
					String edu = datas[6];
					String offerNumber = datas[7];
					String jobInfo = datas[8];
					String companyType = datas[9];
					String staffNumber = datas[10];
					String companyOrientation = datas[11];
					
					//将英文中's替换为/'s，防止数据库插入问题
					jobInfo = jobInfo.replace("\'", "\"");
					JobBean jobBean = new JobBean(
							jobName, company, address,
							salary, date, exp,
							edu, offerNumber, jobInfo,
							companyType, staffNumber,companyOrientation);
//					System.out.println(jobBean);
					//放入容器
					jobBeanList.add(jobBean);
				}catch(Exception e) {
//					System.out.println("本地读取筛选：有问题需要跳过的数据行："+str);
					continue;
				}
			}
			System.out.println("读取完成,一共读取"+jobBeanList.size()+"条信息");
			return jobBeanList;
		}catch(Exception e) {
			System.out.println("读取JobBean失败");
			e.printStackTrace();
		}
		return jobBeanList;
	}
}
