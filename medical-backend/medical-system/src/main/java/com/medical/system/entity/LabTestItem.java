package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 实验室检查明细实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("lab_test_item")
public class LabTestItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 检查ID
     */
    private Long testId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 测定值
     */
    private String itemValue;

    /**
     * 单位
     */
    private String unit;

    /**
     * 参考范围
     */
    private String referenceRange;

    /**
     * 是否异常(1:是 0:否)
     */
    private Integer isAbnormal;

    /**
     * 异常标识(H:高 L:低)
     */
    private String abnormalFlag;
}
