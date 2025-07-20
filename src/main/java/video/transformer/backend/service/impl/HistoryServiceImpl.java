package video.transformer.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import video.transformer.backend.config.UserHolder;
import video.transformer.backend.entity.Artifact;
import video.transformer.backend.entity.History;
import video.transformer.backend.entity.Showroom;
import video.transformer.backend.mapper.HistoryMapper;
import video.transformer.backend.service.ArtifactService;
import video.transformer.backend.service.HistoryService;
import video.transformer.backend.service.IUserService;
import video.transformer.backend.service.ShowroomService;
import video.transformer.backend.utils.FileUtil;
import video.transformer.backend.utils.HttpUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import video.transformer.backend.entity.User;

import static video.transformer.backend.config.UserHolder.userInRoomMap;


@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements HistoryService {

    @Autowired
    private ArtifactService artifactsService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ShowroomService showroomService;

    @Override
    public Artifact addHistory(MultipartFile videoFile, String text) {
        Map<String, Object> map = HttpUtil.postData(text);
        if (map != null) {
            String link = null;
            if (videoFile != null) {
                link = FileUtil.upload(videoFile);
            }
            String artifactsId = (String) map.get("id");
            Integer currentUserId = UserHolder.getUserId();
            
            // 获取文物信息并更新用户坐标
            Artifact artifact = artifactsService.getById(artifactsId);
            if (artifact != null && artifact.getLongitude() != null && artifact.getLatitude() != null) {
                User user = new User();
                user.setId(currentUserId);
                float userLng = (float) (artifact.getLongitude()*1.0f);
                float userLat = (float) (artifact.getLatitude()*1.0f);
                user.setLng(userLng);
                user.setLat(userLat);
                // 更新用户坐标缓存
                UserHolder.setUserCoordinates(currentUserId, userLng, userLat);
                
                // 获取展厅信息并更新展厅坐标
                if (artifact.getShowroomId() != null) {
                    Showroom showroom = showroomService.getById(artifact.getShowroomId());
                    if (showroom != null && showroom.getLng() != null && showroom.getLat() != null) {
                        float roomLng = showroom.getLng().floatValue();
                        float roomLat = showroom.getLat().floatValue();
                        user.setRoomLng(roomLng);
                        user.setRoomLat(roomLat);
                        // 更新展厅坐标缓存
                        UserHolder.setRoomCoordinates(currentUserId, roomLng, roomLat);
                    }
                    userInRoomMap.put(currentUserId, artifact.getShowroomId());
                }

                //boolean success = userService.updateById(user);
                //System.out.println("更新操作返回值：" + success); // false表示未找到记录
            }
            List<History> existData = this.lambdaQuery().isNull(History::getVideoLink).eq(History::getArtifacts_id, artifactsId).list();
            if (existData.isEmpty()) {
                History history = new History();
                history.setVideoLink(null);
                history.setTextMeans(null);
                history.setConvertText(text);
                history.setArtifacts_id(Integer.parseInt(artifactsId));
                this.save(history);
            }

            if (link != null) {
                List<History> list = this.lambdaQuery().isNull(History::getVideoLink).list();
                for (History h : list) {
                    h.setVideoLink(link);
                }
                this.updateBatchById(list);
            }
            return artifact;

        }
        return null;
    }

    @Override
    public List<History> listData() {
        List<History> list = this.lambdaQuery().isNotNull(History::getVideoLink).list();
        Map<String, List<History>> collect = list.stream().collect(Collectors.groupingBy(History::getVideoLink));
        return collect.entrySet().stream().map(entry -> {
            String key = entry.getKey();
            List<History> entryValue = entry.getValue();
            Set<String> meanList = entryValue.stream().filter(x -> x.getTextMeans() != null)
                .map(x -> x.getTextMeans().split("，")[0]).collect(Collectors.toSet());
            History history  = new History();
            history.setVideoLink(key);
            history.setConvertText(entryValue.get(entryValue.size() - 1).getConvertText());
            history.setTextMeans(meanList.stream().collect(Collectors.joining(",")));
            return history;
        }).toList();

    }
}
