package react.reply.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    private JWTUtil jwtUtil;

    public APILoginSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil; // 생성자를 통해 JWT 유틸리티 주입
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("Login Success...."); // 로그인 성공 로그
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // JSON 응답 설정

        log.info(authentication.getName()); // 인증된 사용자명(client_id) 로그

        // JWT 클레임 데이터 설정
        Map<String, Object> claim = Map.of("client_id", authentication.getName());

        // Access Token 발급 (유효기간 1일)
        String accessToken = jwtUtil.generationToken(claim, 1);

        // 응답에 토큰 포함
        Map<String, String> keyMap = Map.of("accessToken", accessToken);
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(keyMap); // Map을 JSON 문자열로 변환

        response.getWriter().print(json); // 응답으로 JSON 반환
    }
}