package bean;

/**
 * @author: PowerZZJ
 * @date: 2019/12/12
 */
public class JobBean {
    private String jobName;
    private String company;
    private String address;
    private String salary;
    private String date;
    private String exp;
    private String edu;
    private String offerNumber;
    private String jobInfo;
    private String companyType;
    private String staffNumber;
    private String companyOrientation;
    private String jobURL;

    public JobBean() {
    }

    @Override
    public String toString() {
        return "jobName=" + jobName + ", company=" + company + ", address=" + address + ", salary=" + salary
                + ", date=" + date + ", exp=" + exp + ", edu=" + edu + ", offerNumber=" + offerNumber + ", companyType=" + companyType + ", staffNumber=" + staffNumber + ", companyOrientation="
                + companyOrientation + ", jobInfo=" + jobInfo + ", jobURL=" + jobURL;
    }


    /**
     * @return 本地保存内容的字符串形式，以5个空格为分割
     */
    public String saveString() {
        return jobName + "     " + company + "     " + address + "     " + salary
                + "     " + date + "     " + exp + "     " + edu + "     " + offerNumber + "     "
                + jobInfo + "     " + companyType + "     " + staffNumber + "     "
                + companyOrientation + "     " + jobURL;
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
    }

    public String getJobURL() {
        return jobURL;
    }

    public void setJobURL(String jobURL) {
        this.jobURL = jobURL;
    }
}
