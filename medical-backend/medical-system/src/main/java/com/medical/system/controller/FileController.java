package com.medical.system.controller;

import com.medical.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "File", description = "文件上传")
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<FileUploadVO> upload(@RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "type", required = false) String type) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        String dir = StringUtils.hasText(uploadDir) ? uploadDir : "uploads";
        Path base = Paths.get(dir).toAbsolutePath();
        Files.createDirectories(base);

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String prefix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String name = prefix + "-" + UUID.randomUUID().toString().replaceAll("-", "");
        String finalName = name + ext;
        Path target = base.resolve(finalName);
        file.transferTo(target.toFile());

        FileUploadVO vo = new FileUploadVO();
        vo.setFileName(finalName);
        vo.setFileUrl("/files/" + finalName);
        vo.setFileSize(file.getSize());
        vo.setType(type);
        return Result.success("上传成功", vo);
    }

    @Data
    public static class FileUploadVO {
        private String fileName;
        private String fileUrl;
        private Long fileSize;
        private String type;
    }
}

