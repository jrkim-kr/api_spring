package react.reply.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Map;

@Log4j2
public class TokenCheckFilter extends OncePerRequestFilter {
    private JWTUtil jwtUtil;

    public TokenCheckFilter(JWTUtil jwUtil) {
        this.jwtUtil = jwUtil; // 생성자를 통해 JWT 유틸리티 주입
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!path.startsWith("/api")) { // /api로 시작하지 않는 요청은 토큰 검증 없이 통과
            filterChain.doFilter(request, response);
            return;
        }
        log.info("Token Check Filter............");
        log.info("JWUtil: "+jwtUtil.toString());

        // AccessToken 검증
        try {
            validateAccessToken(request); // 토큰 검증 메소드 호출
            filterChain.doFilter(request, response); // 검증 성공 시 다음 필터로 요청 전달
        } catch (AccessTokenException e) {
            e.sendResponseError(response); // 검증 실패 시 에러 응답 반환
        }
    }

    // AccessToken 검증
    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String headerStr = request.getHeader("Authorization"); // Authorization 헤더 가져오기
        if (headerStr == null || headerStr.length() < 8) { // 헤더가 없거나 너무 짧은 경우
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        // Bearer 접두사 확인 및 토큰 추출
        String tokenType = headerStr.substring(0,6);
        String tokenStr = headerStr.substring(7);

        if (tokenType.equalsIgnoreCase("Bearer") == false) { // Bearer 타입이 아닌 경우
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            Map<String, Object> value = jwtUtil.validateToken(tokenStr); // 토큰 검증
            return value; // 검증 성공 시 페이로드 반환
        } catch (MalformedJwtException e) { // 잘못된 형식의 JWT
            log.info("MalformedJwtException................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException e) { // 서명 검증 실패
            log.info("SignaturedException..................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch(ExpiredJwtException e) { // 만료된 토큰
            log.info("ExpiredJwtException..................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        } catch (Exception e) { // 기타 예외
            log.info("Exception......................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }
    }
}
