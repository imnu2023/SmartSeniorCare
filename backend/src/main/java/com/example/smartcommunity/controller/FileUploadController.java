package com.example.smartcommunity.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.example.smartcommunity.config.OssConfig;
import com.example.smartcommunity.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    @PostMapping("/health-report")
    public ApiResponse<String> uploadHealthReport(@RequestParam("file") MultipartFile file) {
        logger.info("接收到文件上传请求, 文件名: {}, 大小: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            logger.warn("上传文件为空");
            return ApiResponse.error("请选择要上传的文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "smart-care/health-report/" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;

            logger.info("准备上传文件到OSS, 文件路径: {}", fileName);
            
            PutObjectRequest request = new PutObjectRequest(ossConfig.getBucketName(), fileName, file.getInputStream());
            ossClient.putObject(request);

            String fileUrl = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + fileName;
            logger.info("文件上传成功, URL: {}", fileUrl);
            
            return ApiResponse.success(fileUrl);
        } catch (IOException e) {
            logger.error("文件读取失败", e);
            return ApiResponse.error("上传失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("OSS上传失败", e);
            return ApiResponse.error("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        logger.info("接收到头像上传请求, 文件名: {}, 大小: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            logger.warn("上传文件为空");
            return ApiResponse.error("请选择要上传的头像");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        if (!extension.toLowerCase().matches("\\.(jpg|jpeg|png|gif)$")) {
            return ApiResponse.error("只支持 JPG、JPEG、PNG、GIF 格式的图片");
        }

        try {
            String fileName = "smart-care/avatar/" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;

            logger.info("准备上传头像到OSS, 文件路径: {}", fileName);
            
            PutObjectRequest request = new PutObjectRequest(ossConfig.getBucketName(), fileName, file.getInputStream());
            ossClient.putObject(request);

            String fileUrl = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + fileName;
            logger.info("头像上传成功, URL: {}", fileUrl);
            
            return ApiResponse.success("上传成功", fileUrl);
        } catch (IOException e) {
            logger.error("文件读取失败", e);
            return ApiResponse.error("上传失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("OSS上传失败", e);
            return ApiResponse.error("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/health-reports")
    public ApiResponse<String> uploadHealthReports(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ApiResponse.error("请选择要上传的文件");
        }

        StringBuilder urls = new StringBuilder();
        int successCount = 0;

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String fileName = "smart-care/health-report/" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;

                PutObjectRequest request = new PutObjectRequest(ossConfig.getBucketName(), fileName, file.getInputStream());
                ossClient.putObject(request);

                String fileUrl = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + fileName;
                
                if (successCount > 0) {
                    urls.append(",");
                }
                urls.append(fileUrl);
                successCount++;
            } catch (IOException e) {
                logger.error("文件上传失败", e);
            }
        }

        if (successCount == 0) {
            return ApiResponse.error("所有文件上传失败");
        }

        return ApiResponse.success(urls.toString());
    }
}