package video.transformer.backend.service.impl;

import video.transformer.backend.entity.User;
import video.transformer.backend.mapper.UserMapper;
import video.transformer.backend.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import video.transformer.backend.config.UserHolder;

import java.util.List;

import static video.transformer.backend.config.UserHolder.userInRoomMap;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private PasswordEncoder encoder;

    @Override
    public User getUserByEmail(String email) {
       return this.lambdaQuery().eq(User::getEmail, email).one();
    }

    @Override
    public boolean updateInfo(User user) {
        User exist = this.getById(user.getId());
        if (StringUtils.hasLength(user.getPassword())) {
            user.setPassword(encoder.encode(user.getPassword()));
            return this.updateById(user);
        }

        user.setPassword(exist.getPassword());
        return this.updateById(user);
    }

    @Override
    public List<User> listRecorderUsers() {
        List<User> recorderUsers = this.lambdaQuery()
                .eq(User::getRole, "recorder")
                .list();

        // 遍历用户列表，优先使用内存中的位置信息
        for (User user : recorderUsers) {
            Integer userId = user.getId();
            // 尝试从UserHolder获取位置信息
            UserHolder.UserLocation location = UserHolder.getUserLocation(userId);
            if (location != null) {
                Float userLng = location.getUserLng();
                Float userLat = location.getUserLat();
                Float roomLng = location.getRoomLng();
                Float roomLat = location.getRoomLat();
                
                // 如果内存中有位置信息，则使用内存中的数据
                if (userLng != null && userLat != null) {
                    user.setLng(userLng);
                    user.setLat(userLat);
                }
                if (roomLng != null && roomLat != null) {
                    user.setRoomLng(roomLng);
                    user.setRoomLat(roomLat);
                    Long roomId =  userInRoomMap.get(userId);
                    user.setRoomId(roomId);
                }
            }
        }

        // 过滤掉没有位置信息的用户
        return recorderUsers.stream()
                .filter(user -> user.getLng() != null && user.getLat() != null)
                .toList();
    }
}
