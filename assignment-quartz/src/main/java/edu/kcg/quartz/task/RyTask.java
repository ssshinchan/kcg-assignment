package edu.kcg.quartz.task;

import org.springframework.stereotype.Component;
import edu.kcg.common.utils.StringUtils;

/**
 * 定时任务调度测试
 * 
 * @author assignment
 */
@Component("ryTask")
public class RyTask
{
    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i)
    {
        System.out.println(StringUtils.format("複数パラメータメソッド実行：文字列型 {}、ブール型 {}、ロング型 {}、フロート型 {}、インテジャー型 {}", s, b, l, d, i));
    }

    public void ryParams(String params)
    {
        System.out.println("パラメータ付きメソッド実行：" + params);
    }

    public void ryNoParams()
    {
        System.out.println("パラメータなしメソッド実行");
    }
}
