package se331.lab.rest.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    @Value("${supabase.storage.bucket}")
    String bucketName;

    @Value("${supabase.storage.endpoint_output}")
    String outputUrl;

    private final S3Client s3Client;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddmmssSSS");

    // 基础上传逻辑
    public String uploadFile(MultipartFile file) throws IOException {
        // 创建临时文件
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        // 生成唯一文件名 (时间戳 + 原文件名)
        String saltFileName = LocalDateTime.now().format(formatter) + "-" + file.getOriginalFilename();

        // 构建上传请求
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(saltFileName)
                .build();

        // 执行上传
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));

        // 生成公开访问 URL
        String url = String.format("%s/%s/%s", outputUrl, bucketName, saltFileName);

        // 删除临时文件
        Files.delete(tempFile);
        return url;
    }

    // Lab 11 推荐的特定于图片的上传方法 (包含格式校验)
    public StorageFileDto uploadImage(MultipartFile file) throws ServletException, IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.isEmpty() && fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            String[] allowedExt = { "jpg", "jpeg", "png", "gif" };
            for (String s : allowedExt) {
                if (extension.equalsIgnoreCase(s)) {
                    String urlName = this.uploadFile(file);
                    return StorageFileDto.builder()
                            .url(urlName)
                            .build();
                }
            }
        }
        throw new ServletException("file must be an image");
    }
}
