package video.transformer.backend.service;

import video.transformer.backend.entity.User;

public interface AuthService {
    String login(String username, String password);

    boolean register(User user);
}
