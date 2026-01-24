package com.medical.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Static resource mapping for uploaded files.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dir = StringUtils.hasText(uploadDir) ? uploadDir : "uploads";
        String abs = Paths.get(dir).toAbsolutePath().toUri().toString();
        // Map /files/** to local upload directory
        registry.addResourceHandler("/files/**")
                .addResourceLocations(abs.endsWith("/") ? abs : abs + "/");
    }
}

