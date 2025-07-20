package video.transformer.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import video.transformer.backend.entity.Artifact;

public interface ArtifactService extends IService<Artifact> {
    
    // 搜索文物
    Page<Artifact> searchArtifacts(String keyword, Page<Artifact> page);
    
    // 根据分类获取文物
    Page<Artifact> getArtifactsByCategory(String category, Page<Artifact> page);
    
    // 根据标签搜索文物
    Page<Artifact> searchByTag(String tag, Page<Artifact> page);
    
    // 根据展厅ID获取文物
    Page<Artifact> getArtifactsByShowroom(Long showroomId, Page<Artifact> page);
} 