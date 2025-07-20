package video.transformer.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import video.transformer.backend.entity.Showroom;
import video.transformer.backend.response.R;
import video.transformer.backend.service.ShowroomService;

import java.util.List;

@RestController
@RequestMapping("/showrooms")
public class ShowroomController {

    @Autowired
    private ShowroomService showroomService;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") Integer page,
                 @RequestParam(defaultValue = "10") Integer size) {
        Page<Showroom> pageInfo = new Page<>(page, size);
        showroomService.page(pageInfo);
        return R.ok(pageInfo);
    }

    @GetMapping("/search")
    public R search(@RequestParam String keyword,
                   @RequestParam(defaultValue = "1") Integer page,
                   @RequestParam(defaultValue = "10") Integer size) {
        Page<Showroom> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Showroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Showroom::getName, keyword)
                .or()
                .like(Showroom::getDescription, keyword);
        showroomService.page(pageInfo, wrapper);
        return R.ok(pageInfo);
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Showroom showroom = showroomService.getById(id);
        return R.ok(showroom);
    }

    @PostMapping
    public R save(@RequestBody Showroom showroom) {
        boolean success = showroomService.save(showroom);
        return success ? R.ok(showroom) : R.error("添加展厅失败");
    }

    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody Showroom showroom) {
        showroom.setId(id);
        boolean success = showroomService.updateById(showroom);
        return success ? R.ok(showroom) : R.error("更新展厅失败");
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        boolean success = showroomService.removeById(id);
        return success ? R.ok() : R.error("删除展厅失败");
    }
}
