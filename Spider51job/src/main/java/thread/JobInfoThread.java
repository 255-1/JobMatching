package thread;

import operation.JobInfoOperation;

import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobInfoThread implements Runnable {
    private List<String> urls;
    private JobInfoOperation jobInfoOperation;

    public JobInfoThread(List<String> urls, JobInfoOperation jobInfoOperation) {
        this.urls = urls;
        this.jobInfoOperation = jobInfoOperation;
    }

    @Override
    public void run() {
        jobInfoOperation.getJobInfo(urls);
    }
}
