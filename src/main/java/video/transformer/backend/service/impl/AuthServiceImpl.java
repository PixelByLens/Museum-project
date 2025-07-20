package video.transformer.backend.service.impl;

import video.transformer.backend.entity.User;
import video.transformer.backend.service.AuthService;
import video.transformer.backend.service.IUserService;
import video.transformer.backend.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private IUserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public String login(String username, String password) {
        User user = userService.getUserByEmail(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return JwtUtil.generateToken(username, user.getId());
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        User userByEmail = userService.getUserByEmail(user.getEmail());
        if (userByEmail == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreateTime(new Date());
            userService.save(user);
            return true;
        }
        return false;
    }
}
