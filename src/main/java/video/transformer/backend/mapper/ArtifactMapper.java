package video.transformer.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import video.transformer.backend.entity.Artifact;

@Mapper
public interface ArtifactMapper extends BaseMapper<Artifact> {
} 