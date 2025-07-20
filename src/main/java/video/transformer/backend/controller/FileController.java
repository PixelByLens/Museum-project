package video.transformer.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import video.transformer.backend.response.R;
import video.transformer.backend.utils.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        return R.ok(FileUtil.upload(file));
    }

    @PostMapping("/upload_img")
    public R uploadImg(@RequestParam("file") MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return R.error("上传的文件为空");
        }

        // 获取文件的MIME类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return R.error("只能上传图片文件");
        }

        // 生成唯一的文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".png";
        String filename = UUID.randomUUID().toString() + extension;

        // 确保上传目录存在
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return R.ok(filename);
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        // 获取文件路径
        Path path = Paths.get(UPLOAD_DIR, filename);
        
        // 读取文件内容
        byte[] content = Files.readAllBytes(path);
        
        // 获取文件的MIME类型
        String contentType = Files.probeContentType(path);
        if (contentType == null || !contentType.startsWith("image/")) {
            contentType = "image/png"; // 默认作为PNG处理
        }
        
        // 构建响应
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(content);
    }
}
