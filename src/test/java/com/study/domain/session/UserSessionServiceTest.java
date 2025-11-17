package com.study.domain.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 사용자 세션 서비스 테스트
 * Redis 통합 테스트
 */
@SpringBootTest
@DisplayName("UserSessionService 테스트")
class UserSessionServiceTest {

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String testSessionId;
    private UserSession testUserSession;

    @BeforeEach
    void setUp() {
        // 테스트용 세션 ID 생성
        testSessionId = "test-session-" + UUID.randomUUID().toString();

        // 테스트용 사용자 세션 생성
        testUserSession = UserSession.builder()
                .userKey(12345L)
                .username("testuser")
                .roles(new String[]{"USER", "ADMIN"})
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .ipAddress("127.0.0.1")
                .userAgent("Mozilla/5.0")
                .metadata("{\"device\":\"desktop\"}")
                .build();
    }

    @Test
    @DisplayName("세션 저장 테스트")
    void testSaveSession() {
        // given & when
        userSessionService.saveSession(testSessionId, testUserSession);

        // then
        Optional<UserSession> savedSession = userSessionService.getSession(testSessionId);
        assertThat(savedSession).isPresent();
        assertThat(savedSession.get().getUserKey()).isEqualTo(12345L);
        assertThat(savedSession.get().getUsername()).isEqualTo("testuser");
        assertThat(savedSession.get().getRoles()).containsExactly("USER", "ADMIN");

        // cleanup
        userSessionService.deleteSession(testSessionId);
    }

    @Test
    @DisplayName("세션 조회 테스트")
    void testGetSession() {
        // given
        userSessionService.saveSession(testSessionId, testUserSession);

        // when
        Optional<UserSession> foundSession = userSessionService.getSession(testSessionId);

        // then
        assertThat(foundSession).isPresent();
        assertThat(foundSession.get().getUserKey()).isEqualTo(testUserSession.getUserKey());
        assertThat(foundSession.get().getUsername()).isEqualTo(testUserSession.getUsername());
        assertThat(foundSession.get().getIpAddress()).isEqualTo("127.0.0.1");

        // cleanup
        userSessionService.deleteSession(testSessionId);
    }

    @Test
    @DisplayName("존재하지 않는 세션 조회 테스트")
    void testGetNonExistentSession() {
        // given
        String nonExistentSessionId = "non-existent-session-id";

        // when
        Optional<UserSession> session = userSessionService.getSession(nonExistentSessionId);

        // then
        assertThat(session).isEmpty();
    }

    @Test
    @DisplayName("세션 삭제 테스트")
    void testDeleteSession() {
        // given
        userSessionService.saveSession(testSessionId, testUserSession);
        assertThat(userSessionService.existsSession(testSessionId)).isTrue();

        // when
        userSessionService.deleteSession(testSessionId);

        // then
        assertThat(userSessionService.existsSession(testSessionId)).isFalse();
        Optional<UserSession> deletedSession = userSessionService.getSession(testSessionId);
        assertThat(deletedSession).isEmpty();
    }

    @Test
    @DisplayName("세션 존재 여부 확인 테스트")
    void testExistsSession() {
        // given
        userSessionService.saveSession(testSessionId, testUserSession);

        // when & then
        assertThat(userSessionService.existsSession(testSessionId)).isTrue();
        assertThat(userSessionService.existsSession("non-existent-session")).isFalse();

        // cleanup
        userSessionService.deleteSession(testSessionId);
    }

    @Test
    @DisplayName("세션 연장 테스트")
    void testExtendSession() throws InterruptedException {
        // given
        userSessionService.saveSession(testSessionId, testUserSession);

        // when
        Thread.sleep(1000); // 1초 대기
        userSessionService.extendSession(testSessionId);

        // then
        assertThat(userSessionService.existsSession(testSessionId)).isTrue();

        // cleanup
        userSessionService.deleteSession(testSessionId);
    }

    @Test
    @DisplayName("여러 세션 동시 관리 테스트")
    void testMultipleSessions() {
        // given
        String sessionId1 = "test-session-1-" + UUID.randomUUID();
        String sessionId2 = "test-session-2-" + UUID.randomUUID();

        UserSession session1 = UserSession.builder()
                .userKey(100L)
                .username("user1")
                .roles(new String[]{"USER"})
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .ipAddress("192.168.1.1")
                .build();

        UserSession session2 = UserSession.builder()
                .userKey(200L)
                .username("user2")
                .roles(new String[]{"ADMIN"})
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .ipAddress("192.168.1.2")
                .build();

        // when
        userSessionService.saveSession(sessionId1, session1);
        userSessionService.saveSession(sessionId2, session2);

        // then
        Optional<UserSession> retrievedSession1 = userSessionService.getSession(sessionId1);
        Optional<UserSession> retrievedSession2 = userSessionService.getSession(sessionId2);

        assertThat(retrievedSession1).isPresent();
        assertThat(retrievedSession1.get().getUserKey()).isEqualTo(100L);

        assertThat(retrievedSession2).isPresent();
        assertThat(retrievedSession2.get().getUserKey()).isEqualTo(200L);

        // cleanup
        userSessionService.deleteSession(sessionId1);
        userSessionService.deleteSession(sessionId2);
    }
}
