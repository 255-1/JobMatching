package StaticUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import Base.JobBean;

/**
 * 将JobBean列表，以SaveString格式存入本地文件中
 * 从本地文件中读取JobBean列表
 * 此模块下的公开函数在有JobBean列表的情况下都可以使用
 * @author PowerZZJ
 * @version Date 2019年11月26日 
 */
public class JobBeanUtils {
	
	/**保存单个JobBean的私有函数
	 * @author PowerZZJ
	 * @param jobBean
	 * @param fileName 本地保存文件名
	 */
	private static void SaveJobBean(JobBean jobBean, String fileName) {
		try(BufferedWriter bw = 
				new BufferedWriter(
						new FileWriter(fileName, true))){
			String row = jobBean.saveString();
			bw.write(row);
			bw.newLine();
			bw.flush();
		}catch(Exception e) {
			System.out.println("保存JobBean失败");
			e.printStackTrace();
		}
	}
	
	
	/**保存JobBean列表到本地文件中
	 * @author PowerZZJ
	 * @param jobBeanList JobBean列表
	 * @param fileName 本地保存文件名
	 */
	public static void SaveJobBeanList(List<JobBean> jobBeanList, String fileName) {
		System.out.println("开始备份JobBean列表到本地"+fileName+"文件中");
		for(JobBean jobBean: jobBeanList) {
			SaveJobBean(jobBean, fileName);
		}
		System.out.println("本地备份完成，一共备份"+jobBeanList.size()+"条信息");
	}
	
	
	/**读取本地备份文件，转成JobBean列表，有简单的判空操作
	 * @author PowerZZJ
	 * @param fileName 本地备份文件名
	 * @return JobBean列表
	 */
	public static List<JobBean> LoadJobBeanList(String fileName){
		List<JobBean> jobBeanList = new ArrayList<>();
		try(BufferedReader br = 
				new BufferedReader(
						new FileReader(fileName))){
			String str = null;
			while((str=br.readLine())!=null){
				String[] rowList = str.split("     ");
				try {
					JobBean jobBean = new JobBean(
							rowList[0],rowList[1],rowList[2],
							rowList[3],rowList[4],rowList[5],
							rowList[6],rowList[7],rowList[8],
							rowList[9],rowList[10],rowList[11],
							rowList[12]);
					jobBeanList.add(jobBean);
				}catch(Exception e) {
					//有空数据的行直接跳过
					continue;
				}
			}
			System.out.println("本地文件一共读取"+jobBeanList.size()+"行数据");
		}catch(Exception e) {
			System.out.println("读取本地备份文件失败");
			e.printStackTrace();
		}
		return jobBeanList;
	}
}
