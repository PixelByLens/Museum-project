package video.transformer.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import video.transformer.backend.entity.Artifact;
import video.transformer.backend.response.R;
import video.transformer.backend.service.ArtifactService;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.MediaType;

import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/artifacts")
public class ArtifactController {

    @Resource
    private ArtifactService artifactService;
    
    //@Value("${file.upload.path}")
    private String uploadPath;

    // 创建文物
    @PostMapping
    public R createArtifact(@RequestBody Artifact artifact) {
        return R.ok(artifactService.save(artifact));
    }

    // 更新文物
    @PutMapping("/{id}")
    public R updateArtifact(@PathVariable Long id, @RequestBody Artifact artifact) {
        artifact.setId(id);
        return R.ok(artifactService.updateById(artifact));
    }

    // 删除文物
    @DeleteMapping("/{id}")
    public R deleteArtifact(@PathVariable Long id) {
        return R.ok(artifactService.removeById(id));
    }

    // 获取单个文物
    @GetMapping("/{id}")
    public R getArtifact(@PathVariable Long id) {
        return R.ok(artifactService.getById(id));
    }

    // 获取所有文物（分页）
    @GetMapping
    public R getAllArtifacts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Artifact> pageParam = new Page<>(page, size);
        Page<Artifact> pageResult = artifactService.page(pageParam);
        return R.ok(pageResult);
    }

    // 搜索文物（分页）
    @GetMapping("/search")
    public R searchArtifacts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Artifact> pageParam = new Page<>(page, size);
        Page<Artifact> pageResult = artifactService.searchArtifacts(keyword, pageParam);
        return R.ok(pageResult);
    }

    // 根据分类获取文物（分页）
    @GetMapping("/category/{category}")
    public R getArtifactsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Artifact> pageParam = new Page<>(page, size);
        Page<Artifact> pageResult = artifactService.getArtifactsByCategory(category, pageParam);
        return R.ok(pageResult);
    }

    // 根据标签搜索文物（分页）
    @GetMapping("/tag/{tag}")
    public R searchByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Artifact> pageParam = new Page<>(page, size);
        Page<Artifact> pageResult = artifactService.searchByTag(tag, pageParam);
        return R.ok(pageResult);
    }

    // 根据展厅ID获取文物列表
    @GetMapping("/showroom/{showroomId}")
    public R getArtifactsByShowroom(
            @PathVariable Long showroomId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "1000") Integer size
    ) {
        Page<Artifact> pageParam = new Page<>(page, size);
        Page<Artifact> pageResult = artifactService.getArtifactsByShowroom(showroomId, pageParam);
        return R.ok(pageResult);
    }

    // 文件上传接口
    @PostMapping("/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }
        
        try {
            // 获取文件名和后缀
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            
            // 确保上传路径是绝对路径
            String absoluteUploadPath = new File(uploadPath).getAbsolutePath();
            
            // 创建文件存储目录
            File uploadDir = new File(absoluteUploadPath);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    return R.error("创建上传目录失败");
                }
            }
            
            // 创建目标文件
            File dest = new File(uploadDir, fileName);
            
            // 保存文件
            file.transferTo(dest);
            
            // 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("url", "/uploads/" + fileName);
            return R.ok(data);
            
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败: " + e.getMessage());
        }
    }

    // 文件下载接口
//    @GetMapping("/uploads/{filename:.+}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
//        try {
//            // 构建文件的完整路径
//            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            // 检查文件是否存在
//            if(resource.exists()) {
//                // 确定文件的MIME类型
//                String contentType = Files.probeContentType(filePath);
//                if(contentType == null) {
//                    contentType = "application/octet-stream";
//                }
//
//                // 返回文件
//                return ResponseEntity.ok()
//                        .contentType(MediaType.parseMediaType(contentType))
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
} 