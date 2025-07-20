package video.transformer.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import video.transformer.backend.entity.Showroom;
import video.transformer.backend.mapper.ShowroomMapper;
import video.transformer.backend.service.ShowroomService;

@Service
public class ShowroomServiceImpl extends ServiceImpl<ShowroomMapper, Showroom> implements ShowroomService {
}
