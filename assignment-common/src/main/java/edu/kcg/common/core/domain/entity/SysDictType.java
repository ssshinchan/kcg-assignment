package edu.kcg.common.core.domain.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import edu.kcg.common.annotation.Excel;
import edu.kcg.common.annotation.Excel.ColumnType;
import edu.kcg.common.core.domain.BaseEntity;

/**
 * 字典类型表 sys_dict_type
 * 
 * @author assignment
 */
public class SysDictType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 字典主键 */
    @Excel(name = "辞書キー", cellType = ColumnType.NUMERIC)
    private Long dictId;

    /** 字典名称 */
    @Excel(name = "辞書名")
    private String dictName;

    /** 字典类型 */
    @Excel(name = "辞書タイプ")
    private String dictType;

    /** 状态（0正常 1停用） */
    @Excel(name = "ステータス", readConverterExp = "0=正常,1=無効化")
    private String status;

    public Long getDictId()
    {
        return dictId;
    }

    public void setDictId(Long dictId)
    {
        this.dictId = dictId;
    }

    @NotBlank(message = "辞書名は必須入力です")
    @Size(min = 0, max = 100, message = "辞書タイプ名は100文字以内で入力してください")
    public String getDictName()
    {
        return dictName;
    }

    public void setDictName(String dictName)
    {
        this.dictName = dictName;
    }

    @NotBlank(message = "辞書タイプは必須入力です")
    @Size(min = 0, max = 100, message = "辞書タイプ種別は100文字以内で入力してください")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "辞書タイプはアルファベットで開始し、半角英小文字、数字、アンダースコアのみで入力してください")
    public String getDictType()
    {
        return dictType;
    }

    public void setDictType(String dictType)
    {
        this.dictType = dictType;
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
            .append("dictId", getDictId())
            .append("dictName", getDictName())
            .append("dictType", getDictType())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
