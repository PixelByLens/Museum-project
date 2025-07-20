package video.transformer.backend.config;


import video.transformer.backend.response.R;
import video.transformer.backend.utils.JwtUtil;
import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class JwtInterceptor implements HandlerInterceptor {


    // 拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = JwtUtil.extractClaims(token);
                if (claims != null) {
                    request.setAttribute("claims", claims);
                    UserHolder.setUserId(Integer.parseInt(claims.get("id").toString()));
                    return true;
                }
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(JSON.toJSONString(R.error(401, "未登陆")));
                return false;
            } catch (Exception e) {
                response.getWriter().write(JSON.toJSONString(R.error(401, "未登陆")));
                return false;
            }
        }
        response.getWriter().write(JSON.toJSONString(R.error(401, "未登陆")));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserHolder.remove();
    }


}
