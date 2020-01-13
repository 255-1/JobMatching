package thread;

import operation.JobUrlOperation;

import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobUrlThread implements Runnable {
    private List<String> urls;
    private JobUrlOperation jobUrlOperation;
    private String keyWord;

    public JobUrlThread(List<String> urls, JobUrlOperation jobUrlOperation, String keyWord) {
        this.urls = urls;
        this.jobUrlOperation = jobUrlOperation;
        this.keyWord = keyWord;
    }

    @Override
    public void run() {
        jobUrlOperation.getJobUrl(urls, keyWord);
    }
}
