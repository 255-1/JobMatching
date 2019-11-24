package Base;

/**
 * @author PowerZZJ
 *流程中的基本单位，记录了职位名称，
 *公司名，地址，工资，
 *发布日期，经验要求，
 *学历要求，招聘人数，职
 *位信息，公司类型，公司人数，公司方向 
 */
public class JobBean {
	//职位名称
	private String jobName;
	//公司名称
	private String company;
	//招聘地址
	private String address;
	//薪水
	private String salary;
	//发布日期
	private String date;
	//工作经验要求
	private String exp;
	//教育程度要求
	private String edu;
	//招聘人数
	private String offerNumber;
	//职位信息
	private String jobInfo;
	//公司类型
	private String companyType;
	//公司人数
	private String staffNumber;
	//公司方向
	private String companyOrientation;
	
	public JobBean() {}

	public JobBean(String jobName, String company, String address, String salary, String date, String exp, String edu,
			String offerNumber, String jobInfo, String companyType, String staffNumber, String companyOrientation) {
		super();
		this.jobName = jobName;
		this.company = company;
		this.address = address;
		this.salary = salary;
		this.date = date;
		this.exp = exp;
		this.edu = edu;
		this.offerNumber = offerNumber;
		this.jobInfo = jobInfo;
		this.companyType = companyType;
		this.staffNumber = staffNumber;
		this.companyOrientation = companyOrientation;
	}



	@Override
	public String toString() {
		return "jobName=" + jobName + ", company=" + company + ", address=" + address + ", salary=" + salary
				+ ", date=" + date + ", exp=" + exp + ", edu=" + edu + ", offerNumber=" + offerNumber + ", jobInfo="
				+ jobInfo + ", companyType=" + companyType + ", staffNumber=" + staffNumber + ", companyOrientation="
				+ companyOrientation;
	}
	
	//本地保存格式，间隔4个英文空格
	public String saveString() {
		return jobName + "    " + company + "    " + address + "    " + salary
				+ "    " + date + "    " + exp + "    " + edu + "    " + offerNumber + "    "
				+ jobInfo + "    " + companyType + "    " + staffNumber + "    "
				+ companyOrientation;
	}

	

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getEdu() {
		return edu;
	}

	public void setEdu(String edu) {
		this.edu = edu;
	}

	public String getOfferNumber() {
		return offerNumber;
	}

	public void setOfferNumber(String offerNumber) {
		this.offerNumber = offerNumber;
	}

	public String getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(String jobInfo) {
		this.jobInfo = jobInfo;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getStaffNumber() {
		return staffNumber;
	}

	public void setStaffNumber(String staffNumber) {
		this.staffNumber = staffNumber;
	}

	public String getCompanyOrientation() {
		return companyOrientation;
	}

	public void setCompanyOrientation(String companyOrientation) {
		this.companyOrientation = companyOrientation;
	};
	
	
	
}
