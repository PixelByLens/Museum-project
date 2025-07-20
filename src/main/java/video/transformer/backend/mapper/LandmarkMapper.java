package video.transformer.backend.mapper;

import video.transformer.backend.entity.Landmark;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LandmarkMapper extends BaseMapper<Landmark> {
}
