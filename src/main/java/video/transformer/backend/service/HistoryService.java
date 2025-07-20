package video.transformer.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import video.transformer.backend.entity.Artifact;
import video.transformer.backend.entity.History;

import java.util.List;
import java.util.Map;

public interface HistoryService extends IService<History> {
    Artifact addHistory(MultipartFile videoFile, String text);

    List<History> listData();
}
