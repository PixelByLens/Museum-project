package video.transformer.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import video.transformer.backend.entity.Artifact;
import video.transformer.backend.mapper.ArtifactMapper;
import video.transformer.backend.service.ArtifactService;

@Service
public class ArtifactServiceImpl extends ServiceImpl<ArtifactMapper, Artifact> implements ArtifactService {
    
    @Override
    public Page<Artifact> searchArtifacts(String keyword, Page<Artifact> page) {
        LambdaQueryWrapper<Artifact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Artifact::getName, keyword)
                .or()
                .like(Artifact::getDescription, keyword)
                .or()
                .like(Artifact::getTags, keyword)
                .orderByDesc(Artifact::getId);
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<Artifact> getArtifactsByCategory(String category, Page<Artifact> page) {
        LambdaQueryWrapper<Artifact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Artifact::getCategory, category)
                .orderByDesc(Artifact::getId);
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<Artifact> searchByTag(String tag, Page<Artifact> page) {
        LambdaQueryWrapper<Artifact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Artifact::getTags, tag)
                .orderByDesc(Artifact::getId);
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<Artifact> getArtifactsByShowroom(Long showroomId, Page<Artifact> page) {
        LambdaQueryWrapper<Artifact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Artifact::getShowroomId, showroomId)
                .orderByDesc(Artifact::getId);
        return this.page(page, queryWrapper);
    }
} 