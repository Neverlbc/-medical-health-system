package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 档案附件
 */
@Data
@TableName("record_attachment")
public class RecordAttachment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recordId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}

