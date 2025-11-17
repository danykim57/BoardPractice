package com.study.domain.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 사용자 세션 관리 서비스
 * Redis를 사용한 세션 저장 및 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "boardpractice:user:session:";
    private static final long SESSION_TIMEOUT = 1800; // 30분 (초 단위)

    /**
     * 세션 키 생성
     * @param sessionId 세션 ID
     * @return Redis 키
     */
    private String getSessionKey(String sessionId) {
        return SESSION_PREFIX + sessionId;
    }

    /**
     * 사용자 세션 저장
     * @param sessionId 세션 ID
     * @param userSession 사용자 세션 정보
     */
    public void saveSession(String sessionId, UserSession userSession) {
        try {
            String key = getSessionKey(sessionId);
            userSession.setLastAccessedAt(LocalDateTime.now());

            redisTemplate.opsForValue().set(key, userSession, SESSION_TIMEOUT, TimeUnit.SECONDS);

            log.debug("세션 저장 완료: sessionId={}, userKey={}", sessionId, userSession.getUserKey());
        } catch (Exception e) {
            log.error("세션 저장 실패: sessionId={}", sessionId, e);
            throw new RuntimeException("세션 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 사용자 세션 조회
     * @param sessionId 세션 ID
     * @return 사용자 세션 정보
     */
    public Optional<UserSession> getSession(String sessionId) {
        try {
            String key = getSessionKey(sessionId);
            Object value = redisTemplate.opsForValue().get(key);

            if (value instanceof UserSession) {
                UserSession session = (UserSession) value;
                // 마지막 접근 시간 갱신
                session.setLastAccessedAt(LocalDateTime.now());
                redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.SECONDS);

                log.debug("세션 조회 완료: sessionId={}, userKey={}", sessionId, session.getUserKey());
                return Optional.of(session);
            }

            log.debug("세션 없음: sessionId={}", sessionId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("세션 조회 실패: sessionId={}", sessionId, e);
            return Optional.empty();
        }
    }

    /**
     * 사용자 세션 삭제
     * @param sessionId 세션 ID
     */
    public void deleteSession(String sessionId) {
        try {
            String key = getSessionKey(sessionId);
            redisTemplate.delete(key);
            log.debug("세션 삭제 완료: sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("세션 삭제 실패: sessionId={}", sessionId, e);
        }
    }

    /**
     * 사용자의 모든 세션 조회
     * @param userKey 사용자 고유 키
     * @return 세션 ID 목록
     */
    public Set<String> getUserSessions(Long userKey) {
        try {
            String pattern = SESSION_PREFIX + "*";
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("사용자 세션 목록 조회 실패: userKey={}", userKey, e);
            return Set.of();
        }
    }

    /**
     * 사용자의 모든 세션 삭제 (로그아웃)
     * @param userKey 사용자 고유 키
     */
    public void deleteAllUserSessions(Long userKey) {
        try {
            Set<String> keys = getUserSessions(userKey);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("사용자 모든 세션 삭제 완료: userKey={}, count={}", userKey, keys.size());
            }
        } catch (Exception e) {
            log.error("사용자 세션 전체 삭제 실패: userKey={}", userKey, e);
        }
    }

    /**
     * 세션 타임아웃 연장
     * @param sessionId 세션 ID
     */
    public void extendSession(String sessionId) {
        try {
            String key = getSessionKey(sessionId);
            redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.SECONDS);
            log.debug("세션 연장 완료: sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("세션 연장 실패: sessionId={}", sessionId, e);
        }
    }

    /**
     * 세션 존재 여부 확인
     * @param sessionId 세션 ID
     * @return 세션 존재 여부
     */
    public boolean existsSession(String sessionId) {
        try {
            String key = getSessionKey(sessionId);
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("세션 존재 확인 실패: sessionId={}", sessionId, e);
            return false;
        }
    }
}
