package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.properties.FileProperties;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private FileProperties fileProperties;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file.getOriginalFilename());

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件扩展名
        String extension = null;
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 生成UUID文件名
        String newFilename = UUID.randomUUID().toString() + (extension != null ? extension : "");

        // 创建上传目录（如果不存在）
        File uploadDir = new File(fileProperties.getUploadDir());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件到本地
        try {
            File dest = new File(uploadDir, newFilename);
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

        // 返回前端可访问的文件地址
        String fileUrl = fileProperties.getAccessUrl() + newFilename;
        log.info("文件上传成功: {}", fileUrl);
        return Result.success(fileUrl);
    }

}
