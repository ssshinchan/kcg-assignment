package edu.kcg.quartz.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.kcg.common.annotation.Log;
import edu.kcg.common.constant.Constants;
import edu.kcg.common.core.controller.BaseController;
import edu.kcg.common.core.domain.AjaxResult;
import edu.kcg.common.core.page.TableDataInfo;
import edu.kcg.common.enums.BusinessType;
import edu.kcg.common.exception.job.TaskException;
import edu.kcg.common.utils.StringUtils;
import edu.kcg.common.utils.poi.ExcelUtil;
import edu.kcg.quartz.domain.SysJob;
import edu.kcg.quartz.service.ISysJobService;
import edu.kcg.quartz.util.CronUtils;
import edu.kcg.quartz.util.ScheduleUtils;

/**
 * 调度任务信息操作处理
 * 
 * @author assignment
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController
{
    @Autowired
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysJob sysJob)
    {
        startPage();
        List<SysJob> list = jobService.selectJobList(sysJob);
        return getDataTable(list);
    }

    /**
     * 导出定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "スケジュールタスク", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob sysJob)
    {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        util.exportExcel(response, list, "スケジュールタスク");
    }

    /**
     * 获取定时任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping(value = "/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId)
    {
        return success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:add')")
    @Log(title = "スケジュールタスク", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return error("タスクの新規追加 '" + job.getJobName() + "' に失敗しました。Cron式が正しくありません");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return error("タスクの新規追加 '" + job.getJobName() + "' に失敗しました。ターゲット文字列で 'rmi' 呼び出しは許可されていません");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return error("タスクの新規追加 '" + job.getJobName() + "' に失敗しました。ターゲット文字列で 'ldap(s)' 呼び出しは許可されていません");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return error("タスクの新規追加 '" + job.getJobName() + "' に失敗しました。ターゲット文字列で 'http(s)' 呼び出しは許可されていません");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return error("タスクの新規追加 '" + job.getJobName() + "' に失敗しました。ターゲット文字列に規約違反があります");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return error("タスクの新規追加 '" + job.getJobName() + "' に失敗しました。ターゲット文字列がホワイトリストに含まれていません");
        }
        job.setCreateBy(getUsername());
        return toAjax(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')")
    @Log(title = "スケジュールタスク", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return error("タスクの変更 '" + job.getJobName() + "' に失敗しました。Cron式が正しくありません");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return error("タスクの変更 '" + job.getJobName() + "' に失敗しました。ターゲット文字列で 'rmi' 呼び出しは許可されていません");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return error("タスクの変更 '" + job.getJobName() + "' に失敗しました。ターゲット文字列で 'ldap(s)' 呼び出しは許可されていません");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return error("タスクの変更 '" + job.getJobName() + "' に失敗しました。ターゲット文字列で 'http(s)' 呼び出しは許可されていません");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return error("タスクの変更 '" + job.getJobName() + "' に失敗しました。ターゲット文字列に規約違反があります");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return error("タスクの変更 '" + job.getJobName() + "' に失敗しました。ターゲット文字列がホワイトリストに含まれていません");
        }
        job.setUpdateBy(getUsername());
        return toAjax(jobService.updateJob(job));
    }

    /**
     * 定时任务状态修改
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "スケジュールタスク", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return toAjax(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "スケジュールタスク", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException
    {
        boolean result = jobService.run(job);
        return result ? success() : error("タスクが存在しないか、または既に期限切れです！");
    }

    /**
     * 删除定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "スケジュールタスク", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException
    {
        jobService.deleteJobByIds(jobIds);
        return success();
    }
}
