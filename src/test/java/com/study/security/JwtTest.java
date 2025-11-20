package com.study.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JWT 토큰 생성 및 검증 테스트")
class JwtTest {

    private Jwt jwt;
    private String issuer;
    private String clientSecret;
    private int expirySeconds;

    @BeforeEach
    void setUp() {
        issuer = "test-issuer";
        clientSecret = "test-secret-key-for-jwt-signing";
        expirySeconds = 3600; // 1시간
        jwt = new Jwt(issuer, clientSecret, expirySeconds);
    }

    @Test
    @DisplayName("유효한 JWT 토큰 검증 성공")
    void verifyValidToken_success() {
        // given - 테스트용 토큰 생성
        String token = createTestToken(12345L, new String[]{"USER", "ADMIN"}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims).isNotNull();
        assertThat(claims.userKey()).isEqualTo(12345L);
        assertThat(claims.roles).containsExactly("USER", "ADMIN");
        assertThat(claims.iat()).isGreaterThan(0);
        assertThat(claims.exp()).isGreaterThan(0);
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 예외 발생")
    void verifyExpiredToken_throwsException() {
        // given - 이미 만료된 토큰 생성 (만료 시간: -1초)
        String expiredToken = createTestToken(12345L, new String[]{"USER"}, -1);

        // when & then
        assertThatThrownBy(() -> jwt.verify(expiredToken))
            .isInstanceOf(TokenExpiredException.class);
    }

    @Test
    @DisplayName("잘못된 서명의 토큰 검증 시 예외 발생")
    void verifyInvalidSignature_throwsException() {
        // given - 다른 secret으로 서명된 토큰
        Algorithm wrongAlgorithm = Algorithm.HMAC512("wrong-secret-key");
        String invalidToken = JWT.create()
            .withIssuer(issuer)
            .withClaim("userKey", 12345L)
            .withArrayClaim("roles", new String[]{"USER"})
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
            .sign(wrongAlgorithm);

        // when & then
        assertThatThrownBy(() -> jwt.verify(invalidToken))
            .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("잘못된 issuer의 토큰 검증 시 예외 발생")
    void verifyInvalidIssuer_throwsException() {
        // given - 다른 issuer로 생성된 토큰
        Algorithm algorithm = Algorithm.HMAC512(clientSecret);
        String invalidIssuerToken = JWT.create()
            .withIssuer("wrong-issuer")
            .withClaim("userKey", 12345L)
            .withArrayClaim("roles", new String[]{"USER"})
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
            .sign(algorithm);

        // when & then
        assertThatThrownBy(() -> jwt.verify(invalidIssuerToken))
            .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Claims 객체에서 userKey 추출")
    void extractUserKeyFromClaims() {
        // given
        String token = createTestToken(99999L, new String[]{"ADMIN"}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims.userKey()).isEqualTo(99999L);
    }

    @Test
    @DisplayName("Claims 객체에서 roles 추출")
    void extractRolesFromClaims() {
        // given
        String[] expectedRoles = {"USER", "ADMIN", "MANAGER"};
        String token = createTestToken(12345L, expectedRoles, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims.roles).containsExactly(expectedRoles);
    }

    @Test
    @DisplayName("Claims 객체에서 발급 시간(iat) 추출")
    void extractIssuedAtFromClaims() {
        // given
        long beforeCreation = System.currentTimeMillis();
        String token = createTestToken(12345L, new String[]{"USER"}, expirySeconds);
        long afterCreation = System.currentTimeMillis();

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims.iat()).isGreaterThanOrEqualTo(beforeCreation);
        assertThat(claims.iat()).isLessThanOrEqualTo(afterCreation);
    }

    @Test
    @DisplayName("Claims 객체에서 만료 시간(exp) 추출")
    void extractExpiresAtFromClaims() {
        // given
        long currentTime = System.currentTimeMillis();
        String token = createTestToken(12345L, new String[]{"USER"}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        long expectedExpiry = currentTime + (expirySeconds * 1000L);
        assertThat(claims.exp()).isGreaterThan(currentTime);
        assertThat(claims.exp()).isLessThanOrEqualTo(expectedExpiry + 1000); // 1초 오차 허용
    }

    @Test
    @DisplayName("Claims.of 정적 팩토리 메서드로 Claims 생성")
    void createClaimsUsingFactoryMethod() {
        // when
        Jwt.Claims claims = Jwt.Claims.of(12345L, "testuser", new String[]{"USER", "ADMIN"});

        // then
        assertThat(claims.userKey()).isEqualTo(12345L);
        assertThat(claims.roles).containsExactly("USER", "ADMIN");
    }

    @Test
    @DisplayName("userKey가 없는 토큰 검증 시 -1 반환")
    void verifyTokenWithoutUserKey_returnsMinusOne() {
        // given - userKey 없이 생성된 토큰
        Algorithm algorithm = Algorithm.HMAC512(clientSecret);
        String tokenWithoutUserKey = JWT.create()
            .withIssuer(issuer)
            .withArrayClaim("roles", new String[]{"USER"})
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
            .sign(algorithm);

        // when
        Jwt.Claims claims = jwt.verify(tokenWithoutUserKey);

        // then
        assertThat(claims.userKey()).isEqualTo(-1L);
    }

    @Test
    @DisplayName("roles가 없는 토큰 검증")
    void verifyTokenWithoutRoles() {
        // given - roles 없이 생성된 토큰
        Algorithm algorithm = Algorithm.HMAC512(clientSecret);
        String tokenWithoutRoles = JWT.create()
            .withIssuer(issuer)
            .withClaim("userKey", 12345L)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
            .sign(algorithm);

        // when
        Jwt.Claims claims = jwt.verify(tokenWithoutRoles);

        // then
        assertThat(claims.userKey()).isEqualTo(12345L);
        assertThat(claims.roles).isNull();
    }

    @Test
    @DisplayName("빈 roles 배열 처리")
    void verifyTokenWithEmptyRoles() {
        // given
        String token = createTestToken(12345L, new String[]{}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims.roles).isEmpty();
    }

    @Test
    @DisplayName("단일 role 처리")
    void verifyTokenWithSingleRole() {
        // given
        String token = createTestToken(12345L, new String[]{"USER"}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims.roles).containsExactly("USER");
    }

    @Test
    @DisplayName("Claims iat 삭제 기능")
    void eraseIatFromClaims() {
        // given
        String token = createTestToken(12345L, new String[]{"USER"}, expirySeconds);
        Jwt.Claims claims = jwt.verify(token);
        long originalIat = claims.iat();

        // when
        claims.eraseIat();

        // then
        assertThat(claims.iat()).isEqualTo(-1L);
        assertThat(originalIat).isGreaterThan(0);
    }

    @Test
    @DisplayName("Claims exp 삭제 기능")
    void eraseExpFromClaims() {
        // given
        String token = createTestToken(12345L, new String[]{"USER"}, expirySeconds);
        Jwt.Claims claims = jwt.verify(token);
        long originalExp = claims.exp();

        // when
        claims.eraseExp();

        // then
        assertThat(claims.exp()).isEqualTo(-1L);
        assertThat(originalExp).isGreaterThan(0);
    }

    @Test
    @DisplayName("Claims toString 메서드")
    void claimsToString() {
        // given
        String token = createTestToken(12345L, new String[]{"USER", "ADMIN"}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);
        String claimsString = claims.toString();

        // then
        assertThat(claimsString).contains("userKey");
        assertThat(claimsString).contains("12345");
        assertThat(claimsString).contains("roles");
    }

    @Test
    @DisplayName("Jwt toString 메서드")
    void jwtToString() {
        // when
        String jwtString = jwt.toString();

        // then
        assertThat(jwtString).isNotNull();
        assertThat(jwtString).contains("Jwt");
    }

    @Test
    @DisplayName("다양한 expirySeconds로 JWT 인스턴스 생성")
    void createJwtWithDifferentExpirySeconds() {
        // when
        Jwt shortLivedJwt = new Jwt(issuer, clientSecret, 60); // 1분
        Jwt longLivedJwt = new Jwt(issuer, clientSecret, 86400); // 1일

        // then - 예외 없이 생성되어야 함
        assertThat(shortLivedJwt).isNotNull();
        assertThat(longLivedJwt).isNotNull();
    }

    @Test
    @DisplayName("매우 긴 userKey 값 처리")
    void handleLargeUserKey() {
        // given
        long largeUserKey = Long.MAX_VALUE;
        String token = createTestToken(largeUserKey, new String[]{"USER"}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims.userKey()).isEqualTo(largeUserKey);
    }

    @Test
    @DisplayName("음수 userKey 값 처리")
    void handleNegativeUserKey() {
        // given
        long negativeUserKey = -999L;
        String token = createTestToken(negativeUserKey, new String[]{"USER"}, expirySeconds);

        // when
        Jwt.Claims claims = jwt.verify(token);

        // then
        assertThat(claims.userKey()).isEqualTo(negativeUserKey);
    }

    /**
     * 테스트용 JWT 토큰 생성 헬퍼 메서드
     */
    private String createTestToken(Long userKey, String[] roles, int expirySeconds) {
        Algorithm algorithm = Algorithm.HMAC512(clientSecret);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + (expirySeconds * 1000L));

        return JWT.create()
            .withIssuer(issuer)
            .withClaim("userKey", userKey)
            .withArrayClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(expiry)
            .sign(algorithm);
    }
}
