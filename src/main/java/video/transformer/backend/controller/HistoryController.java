package video.transformer.backend.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import video.transformer.backend.response.R;
import video.transformer.backend.service.HistoryService;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @Resource
    private HistoryService historyService;

    @GetMapping
    public R getHistory() {
        return R.ok(historyService.listData());
    }

    @PostMapping("/add")
    public R addHistory(@RequestParam(value = "file", required = false) MultipartFile videoFile,
                        @RequestParam("text") String text){
        return R.ok(historyService.addHistory(videoFile, text));
    }
}
