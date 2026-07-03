package edu.kcg.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import edu.kcg.common.annotation.Excel;
import edu.kcg.common.annotation.Excel.ColumnType;
import edu.kcg.common.core.domain.BaseEntity;

/**
 * 操作日志记录表 oper_log
 * 
 * @author assignment
 */
public class SysOperLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    @Excel(name = "操作番号", cellType = ColumnType.NUMERIC)
    private Long operId;

    /** 操作模块 */
    @Excel(name = "操作モジュール")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @Excel(name = "業務タイプ", readConverterExp = "0=その他,1=新規追加,2=変更,3=削除,4=権限付与,5=エクスポート,6=インポート,7=強制ログアウト,8=コード生成,9=データクリア")
    private Integer businessType;

    /** 业务类型数组 */
    private Integer[] businessTypes;

    /** 请求方法 */
    @Excel(name = "リクエストメソッド")
    private String method;

    /** 请求方式 */
    @Excel(name = "リクエストタイプ")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @Excel(name = "操作カテゴリ", readConverterExp = "0=その他,1=管理者,2=モバイルユーザー")
    private Integer operatorType;

    /** 操作人员 */
    @Excel(name = "操作担当者")
    private String operName;

    /** 部门名称 */
    @Excel(name = "部門名")
    private String deptName;

    /** 请求url */
    @Excel(name = "リクエストアドレス")
    private String operUrl;

    /** 操作地址 */
    @Excel(name = "操作アドレス")
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作場所")
    private String operLocation;

    /** 请求参数 */
    @Excel(name = "リクエストパラメータ")
    private String operParam;

    /** 返回参数 */
    @Excel(name = "レスポンスパラメータ")
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @Excel(name = "ステータス", readConverterExp = "0=正常,1=エラー")
    private Integer status;

    /** 错误消息 */
    @Excel(name = "エラーメッセージ")
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作時間", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    /** 消耗时间 */
    @Excel(name = "所要時間", suffix = "ミリ秒")
    private Long costTime;

    public Long getOperId()
    {
        return operId;
    }

    public void setOperId(Long operId)
    {
        this.operId = operId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(Integer businessType)
    {
        this.businessType = businessType;
    }

    public Integer[] getBusinessTypes()
    {
        return businessTypes;
    }

    public void setBusinessTypes(Integer[] businessTypes)
    {
        this.businessTypes = businessTypes;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    public Integer getOperatorType()
    {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType)
    {
        this.operatorType = operatorType;
    }

    public String getOperName()
    {
        return operName;
    }

    public void setOperName(String operName)
    {
        this.operName = operName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getOperUrl()
    {
        return operUrl;
    }

    public void setOperUrl(String operUrl)
    {
        this.operUrl = operUrl;
    }

    public String getOperIp()
    {
        return operIp;
    }

    public void setOperIp(String operIp)
    {
        this.operIp = operIp;
    }

    public String getOperLocation()
    {
        return operLocation;
    }

    public void setOperLocation(String operLocation)
    {
        this.operLocation = operLocation;
    }

    public String getOperParam()
    {
        return operParam;
    }

    public void setOperParam(String operParam)
    {
        this.operParam = operParam;
    }

    public String getJsonResult()
    {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult)
    {
        this.jsonResult = jsonResult;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public Date getOperTime()
    {
        return operTime;
    }

    public void setOperTime(Date operTime)
    {
        this.operTime = operTime;
    }

    public Long getCostTime()
    {
        return costTime;
    }

    public void setCostTime(Long costTime)
    {
        this.costTime = costTime;
    }
}
