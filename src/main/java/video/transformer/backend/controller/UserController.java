package video.transformer.backend.controller;

import video.transformer.backend.config.UserHolder;
import video.transformer.backend.entity.User;
import video.transformer.backend.response.R;
import video.transformer.backend.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private IUserService userService;

    @GetMapping("/{id}")
    public R getInfo(@PathVariable Integer id) {
        return R.ok(userService.getById(id));
    }


    @GetMapping("/profile")
    public R profile (){
        return R.ok(userService.getById(UserHolder.getUserId()));
    }

    @PutMapping("/")
    public R updateUser(@RequestBody User user) {
        return R.ok(userService.updateInfo(user));
    }

    @GetMapping("/recorders")
    public R listRecorders() {
        return R.ok(userService.listRecorderUsers());
    }

    @GetMapping("/list")
    public R listUsers() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return R.ok(userService.list(queryWrapper));
    }

    @PostMapping("/")
    public R createUser(@RequestBody User user) {
        // 设置默认值
        user.setCreateTime(new java.util.Date());
        user.setImg("");
        user.setPhone("");
        user.setAddress("");
        user.setLng(0.0f);
        user.setLat(0.0f);
        
        // 使用email作为nick
        user.setNick(user.getEmail());
        
        return R.ok(userService.save(user));
    }

    @DeleteMapping("/{id}")
    public R deleteUser(@PathVariable Integer id) {
        return R.ok(userService.removeById(id));
    }

    @PutMapping("/role")
    public R updateUserRole(@RequestBody User user) {
        User existingUser = userService.getById(user.getId());
        if (existingUser == null) {
            return R.error("用户不存在");
        }
        existingUser.setRole(user.getRole());
        return R.ok(userService.updateById(existingUser));
    }
}
