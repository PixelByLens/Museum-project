package video.transformer.backend.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtil {

    public static String upload(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 通过 UUID 生成随机文件名字，防止重复
        String randomFileName = UUID.randomUUID() + fileExtension;
        String filePath = System.getProperty("user.dir") + "/src/main/resources/static/video/";
        File dest = new File(filePath + randomFileName);

        try {
            file.transferTo(dest);
            return "/video/" + randomFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadImg(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 通过 UUID 生成随机文件名字，防止重复
        String randomFileName = UUID.randomUUID() + fileExtension;
        String filePath = System.getProperty("user.dir") + "/images";
        if(!new File(filePath).exists()){
            new File(filePath).mkdirs();
        }
        File dest = new File(filePath + randomFileName);

        try {
            file.transferTo(dest);
            return randomFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
