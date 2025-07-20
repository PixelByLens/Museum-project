package video.transformer.backend.service;

import video.transformer.backend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IUserService extends IService<User> {

    User getUserByEmail(String email);

    boolean updateInfo(User user);

    List<User> listRecorderUsers();

}
