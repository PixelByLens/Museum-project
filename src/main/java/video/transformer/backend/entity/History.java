package video.transformer.backend.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_history")
public class History {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String videoLink;

    private String convertText;

    private String textMeans;

    //匹配到的文物id
    private Integer artifacts_id;

    public Integer getArtifacts_id() {
        return artifacts_id;
    }

    public void setArtifacts_id(Integer artifacts_id) {
        this.artifacts_id = artifacts_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getConvertText() {
        return convertText;
    }

    public void setConvertText(String convertText) {
        this.convertText = convertText;
    }

    public String getTextMeans() {
        return textMeans;
    }

    public void setTextMeans(String textMeans) {
        this.textMeans = textMeans;
    }
}
