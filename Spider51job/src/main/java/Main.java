import task.ProcedureTimeTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * @author: PowerZZJ
 * @date: 2020/1/12
 */
public class Main {
    public static void main(String[] args) {
        ProcedureTimeTask timeTask = new ProcedureTimeTask();
        Timer timer = new Timer();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        //设置定时任务，从现在开始，每24小时执行一次
        timer.schedule(timeTask, date, 24*60*60*1000);
    }
}
