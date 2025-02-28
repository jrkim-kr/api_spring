package react.reply.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {
    @Value("${jwt.secret.key}")
    private String key; // application.properties에서 JWT 비밀키를 가져옴

    // JWT 문자열 생성
    public String generationToken(Map<String, Object> valueMap, int days) {
        log.info("generateKey..."+key); // 로그에 키 정보 출력 (실제 운영에서는 제거 필요)

        // header 설정
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT"); // 토큰 타입 설정
        headers.put("alg", "HS256"); // 암호화 알고리즘 설정

        // payload 설정
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap); // 전달된 데이터를 payload에 추가

        // 유효기간 설정 (일 단위)
        int time = 60*24*days; // 분 단위로 계산 (60분 * 24시간 * days)

        // JWT 생성
        String jwtStr = Jwts.builder()
                .setHeader(headers) // 헤더 설정
                .setClaims(payloads) // payload 설정
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) // 발급 시간
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant())) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, key.getBytes()) // 서명 알고리즘과 키 설정
                .compact(); // 최종 JWT 문자열 생성

        return jwtStr;
    }

    // 토큰 검증
    public Map<String, Object> validateToken(String token) throws Exception {
        // JWT 파싱 및 검증 후 본문(body) 반환
        return Jwts.parser()
                .setSigningKey(key.getBytes()) // 서명 검증에 사용할 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱 및 서명 검증
                .getBody(); // 검증 성공 시 payload 반환
    }
}