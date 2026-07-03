package edu.kcg.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import edu.kcg.common.annotation.Excel;
import edu.kcg.common.annotation.Excel.ColumnType;
import edu.kcg.common.core.domain.BaseEntity;

/**
 * 参数配置表 sys_config
 * 
 * @author assignment
 */
public class SysConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 参数主键 */
    @Excel(name = "パラメータ主キー", cellType = ColumnType.NUMERIC)
    private Long configId;

    /** 参数名称 */
    @Excel(name = "パラメータ名")
    private String configName;

    /** 参数键名 */
    @Excel(name = "パラメータキー名")
    private String configKey;

    /** 参数键值 */
    @Excel(name = "パラメータ値")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @Excel(name = "システム組み込み", readConverterExp = "Y=はい,N=いいえ")
    private String configType;

    public Long getConfigId()
    {
        return configId;
    }

    public void setConfigId(Long configId)
    {
        this.configId = configId;
    }

    @NotBlank(message = "パラメータ名は必須入力です")
    @Size(min = 0, max = 100, message = "パラメータ名は100文字以内で入力してください")
    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    @NotBlank(message = "パラメータキー名は必須入力です")
    @Size(min = 0, max = 100, message = "パラメータキー名は100文字以内で入力してください")
    public String getConfigKey()
    {
        return configKey;
    }

    public void setConfigKey(String configKey)
    {
        this.configKey = configKey;
    }

    @NotBlank(message = "パラメータ値は必須入力です")
    @Size(min = 0, max = 500, message = "パラメータ値は500文字以内で入力してください")
    public String getConfigValue()
    {
        return configValue;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }

    public String getConfigType()
    {
        return configType;
    }

    public void setConfigType(String configType)
    {
        this.configType = configType;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("configName", getConfigName())
            .append("configKey", getConfigKey())
            .append("configValue", getConfigValue())
            .append("configType", getConfigType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
