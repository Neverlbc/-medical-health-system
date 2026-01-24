package com.medical.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.PatientRecord;
import com.medical.system.entity.RecordAttachment;
import com.medical.system.mapper.RecordAttachmentMapper;
import com.medical.system.service.PatientRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 健康档案接口
 */
@RestController
@RequestMapping("/api/v1/record")
@Tag(name = "Record", description = "健康档案")
@RequiredArgsConstructor
@Validated
public class RecordController {

    private final PatientRecordService recordService;
    private final RecordAttachmentMapper attachmentMapper;

    @PostMapping
    @Operation(summary = "创建档案")
    public Result<Void> create(@RequestBody @Valid PatientRecord record) {
        Long uid = SecurityUtils.getUserId();
        String role = SecurityUtils.getAuthentication() != null ? SecurityUtils.getAuthentication().getAuthorities().toString() : null;
        String roleSimple = role != null && role.contains("ROLE_DOCTOR") ? "DOCTOR" : (role != null && role.contains("ROLE_ADMIN") ? "ADMIN" : "PATIENT");
        recordService.create(record, uid, roleSimple);
        return Result.success("ok");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新档案")
    public Result<Void> update(@PathVariable @NotNull Long id, @RequestBody @Valid PatientRecord record) {
        Long uid = SecurityUtils.getUserId();
        String role = SecurityUtils.getAuthentication() != null ? SecurityUtils.getAuthentication().getAuthorities().toString() : null;
        String roleSimple = role != null && role.contains("ROLE_DOCTOR") ? "DOCTOR" : (role != null && role.contains("ROLE_ADMIN") ? "ADMIN" : "PATIENT");
        record.setId(id);
        recordService.update(record, uid, roleSimple);
        return Result.success("ok");
    }

    @GetMapping("/{id}")
    @Operation(summary = "档案详情")
    public Result<PatientRecord> detail(@PathVariable @NotNull Long id) {
        Long uid = SecurityUtils.getUserId();
        String role = SecurityUtils.getAuthentication() != null ? SecurityUtils.getAuthentication().getAuthorities().toString() : null;
        String roleSimple = role != null && role.contains("ROLE_DOCTOR") ? "DOCTOR" : (role != null && role.contains("ROLE_ADMIN") ? "ADMIN" : "PATIENT");
        return Result.success(recordService.detail(id, uid, roleSimple));
    }

    @GetMapping("/list")
    @Operation(summary = "档案列表")
    public Result<IPage<PatientRecord>> list(@RequestParam(defaultValue = "1") long pageNum,
                                             @RequestParam(defaultValue = "10") long pageSize,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Long userId) {
        Long uid = SecurityUtils.getUserId();
        String role = SecurityUtils.getAuthentication() != null ? SecurityUtils.getAuthentication().getAuthorities().toString() : null;
        String roleSimple = role != null && role.contains("ROLE_DOCTOR") ? "DOCTOR" : (role != null && role.contains("ROLE_ADMIN") ? "ADMIN" : "PATIENT");
        Page<PatientRecord> page = new Page<>(pageNum, pageSize);
        return Result.success(recordService.pageList(page, keyword, userId, uid, roleSimple));
    }

    // -------- Attachments --------
    @GetMapping("/{id}/attachments")
    @Operation(summary = "附件列表")
    public Result<java.util.List<RecordAttachment>> attachments(@PathVariable @NotNull Long id) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RecordAttachment> qw =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        qw.eq(RecordAttachment::getRecordId, id);
        return Result.success(attachmentMapper.selectList(qw));
    }

    @PostMapping("/{id}/attachments")
    @Operation(summary = "新增附件")
    public Result<Void> addAttachment(@PathVariable @NotNull Long id, @RequestBody @Valid AttachmentCreateReq req) {
        RecordAttachment att = new RecordAttachment();
        att.setRecordId(id);
        att.setFileName(req.getFileName());
        att.setFileUrl(req.getFileUrl());
        att.setFileType(req.getFileType());
        att.setFileSize(req.getFileSize());
        attachmentMapper.insert(att);
        return Result.success("ok");
    }

    @DeleteMapping("/attachments/{attId}")
    @Operation(summary = "删除附件")
    public Result<Void> deleteAttachment(@PathVariable @NotNull Long attId) {
        attachmentMapper.deleteById(attId);
        return Result.success("ok");
    }

    @Data
    public static class AttachmentCreateReq {
        @NotNull
        private String fileName;
        @NotNull
        private String fileUrl;
        private String fileType;
        private Long fileSize;
    }
}
