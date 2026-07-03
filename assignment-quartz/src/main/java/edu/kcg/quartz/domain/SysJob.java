package edu.kcg.quartz.domain;

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import edu.kcg.common.annotation.Excel;
import edu.kcg.common.annotation.Excel.ColumnType;
import edu.kcg.common.constant.ScheduleConstants;
import edu.kcg.common.core.domain.BaseEntity;
import edu.kcg.common.utils.StringUtils;
import edu.kcg.quartz.util.CronUtils;

/**
 * 定时任务调度表 sys_job
 * 
 * @author assignment
 */
public class SysJob extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @Excel(name = "タスク番号", cellType = ColumnType.NUMERIC)
    private Long jobId;

    /** 任务名称 */
    @Excel(name = "タスク名")
    private String jobName;

    /** 任务组名 */
    @Excel(name = "タスクグループ名")
    private String jobGroup;

    /** 调用目标字符串 */
    @Excel(name = "呼び出し先ターゲット文字列")
    private String invokeTarget;

    /** cron执行表达式 */
    @Excel(name = "実行式 ")
    private String cronExpression;

    /** cron计划策略 */
    @Excel(name = "スケジュールポリシー ", readConverterExp = "0=デフォルト,1=即時トリガー実行,2=1回のみトリガー実行,3=即時実行なし")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /** 是否并发执行（0允许 1禁止） */
    @Excel(name = "同時実行", readConverterExp = "0=許可,1=禁止")
    private String concurrent;

    /** 任务状态（0正常 1暂停） */
    @Excel(name = "タスク状態", readConverterExp = "0=正常,1=一時停止")
    private String status;

    public Long getJobId()
    {
        return jobId;
    }

    public void setJobId(Long jobId)
    {
        this.jobId = jobId;
    }

    @NotBlank(message = "タスク名は必須入力です")
    @Size(min = 0, max = 64, message = "タスク名は64文字以内で入力してください")
    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getJobGroup()
    {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup)
    {
        this.jobGroup = jobGroup;
    }

    @NotBlank(message = "呼び出し先ターゲット文字列は必須入力です")
    @Size(min = 0, max = 500, message = "呼び出し先ターゲット文字列は500文字以内で入力してください")
    public String getInvokeTarget()
    {
        return invokeTarget;
    }

    public void setInvokeTarget(String invokeTarget)
    {
        this.invokeTarget = invokeTarget;
    }

    @NotBlank(message = "Cron実行式は必須入力です")
    @Size(min = 0, max = 255, message = "Cron実行式は255文字以内で入力してください")
    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime()
    {
        if (StringUtils.isNotEmpty(cronExpression))
        {
            return CronUtils.getNextExecution(cronExpression);
        }
        return null;
    }

    public String getMisfirePolicy()
    {
        return misfirePolicy;
    }

    public void setMisfirePolicy(String misfirePolicy)
    {
        this.misfirePolicy = misfirePolicy;
    }

    public String getConcurrent()
    {
        return concurrent;
    }

    public void setConcurrent(String concurrent)
    {
        this.concurrent = concurrent;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("jobId", getJobId())
            .append("jobName", getJobName())
            .append("jobGroup", getJobGroup())
            .append("cronExpression", getCronExpression())
            .append("nextValidTime", getNextValidTime())
            .append("misfirePolicy", getMisfirePolicy())
            .append("concurrent", getConcurrent())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
