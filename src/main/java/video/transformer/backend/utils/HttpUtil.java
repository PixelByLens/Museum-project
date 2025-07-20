package video.transformer.backend.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    public static Map<String, Object> postData(String body) {
        try {
            String url = "http://127.0.0.1:8686/match";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> data = new HashMap<>();
            data.put("text", body);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(data, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            return response.getBody();
        } catch (Exception e) {
            return null;
        }

    }
//
//    public static void main(String[] args) {
//        Map<String, Object> map = postData("新石器时代陶罐 [7]，史前陶器，中国一级文物，1985年河南郑州出土，现收藏于河南博物院");
//        System.out.println(map);
//    }
}
