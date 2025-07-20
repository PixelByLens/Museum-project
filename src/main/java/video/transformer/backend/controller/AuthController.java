package video.transformer.backend.controller;


import video.transformer.backend.entity.User;
import video.transformer.backend.response.R;
import video.transformer.backend.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Resource
    private AuthService authService;

    // 登录
    @PostMapping("/login")
    public R login(@RequestParam String username,
                   @RequestParam String password) {
        String token = authService.login(username, password);
        if (token == null) {
            return R.error(500, "账户或密码错误");
        }
        return R.ok(token);
    }

    // 注册
    @PostMapping("/register")
    public R register(@RequestBody User user) {
        user.setRole("viewer");
        if (!authService.register(user)) {
            return R.error( "账户已被注册");
        }
        return R.ok();
    }


}
