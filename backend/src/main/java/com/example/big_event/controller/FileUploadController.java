package com.example.big_event.controller;

import com.example.big_event.pojo.Result;
import com.example.big_event.utils.FileValidationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {
    @Value("${upload.allowed-extensions}")
    List<String> allowedExtensions;
    @Value("${upload.allowed-content-types}")
    List<String> allowedContentTypes;
    @Value("${real.path}")
    private String realPath;
    @Value("${web.prefix}")
    private String webPath;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {


        // 2. 后缀 + MIME + 魔数 + 大小校验
        FileValidationUtil.validate(file, allowedExtensions, allowedContentTypes);
        //把文件的内容存储到本地磁盘上
        String originalFilename = file.getOriginalFilename();
        //保证文件名字是唯一的防止文件覆盖
        String filename = UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        //按配置的目录落盘，目录不存在时自动创建
        Path dir = Paths.get(realPath).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        file.transferTo(dir.resolve(filename).toFile());
        //按当前请求的域名/端口拼接访问地址，避免写死localhost
        String prefix = webPath.startsWith("/") ? webPath : "/" + webPath;
        if(!prefix.endsWith("/")){
            prefix = prefix + "/";
        }
        String url = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(prefix)
                .path(filename)
                .toUriString();
        return Result.success(url);
    }
}
