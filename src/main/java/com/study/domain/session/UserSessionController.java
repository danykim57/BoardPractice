package com.study.domain.session;

import com.study.common.dto.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 세션 관리 REST API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class UserSessionController {

    private final UserSessionService userSessionService;

    /**
     * 세션 생성 (로그인 시뮬레이션)
     * @param request HTTP 요청
     * @param userKey 사용자 고유 키
     * @param username 사용자 이름
     * @return 세션 정보
     */
    @PostMapping("/create")
    public GenericResponse<Map<String, Object>> createSession(
            HttpServletRequest request,
            @RequestParam Long userKey,
            @RequestParam String username) {

        try {
            HttpSession httpSession = request.getSession(true);
            String sessionId = httpSession.getId();

            // 사용자 세션 정보 생성
            UserSession userSession = UserSession.builder()
                    .userKey(userKey)
                    .username(username)
                    .roles(new String[]{"USER"})
                    .createdAt(LocalDateTime.now())
                    .lastAccessedAt(LocalDateTime.now())
                    .ipAddress(getClientIpAddress(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            // Redis에 세션 저장
            userSessionService.saveSession(sessionId, userSession);

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("userKey", userKey);
            response.put("username", username);
            response.put("message", "세션이 생성되었습니다.");

            log.info("세션 생성: sessionId={}, userKey={}", sessionId, userKey);

            return new GenericResponse<>(response);
        } catch (Exception e) {
            log.error("세션 생성 실패", e);
            return new GenericResponse<>(null, "세션 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 세션 조회
     * @param request HTTP 요청
     * @return 세션 정보
     */
    @GetMapping("/info")
    public GenericResponse<Map<String, Object>> getSessionInfo(HttpServletRequest request) {
        try {
            HttpSession httpSession = request.getSession(false);

            if (httpSession == null) {
                return new GenericResponse<>(null, "세션이 존재하지 않습니다.");
            }

            String sessionId = httpSession.getId();
            Optional<UserSession> userSessionOpt = userSessionService.getSession(sessionId);

            if (userSessionOpt.isEmpty()) {
                return new GenericResponse<>(null, "세션 정보를 찾을 수 없습니다.");
            }

            UserSession userSession = userSessionOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("userKey", userSession.getUserKey());
            response.put("username", userSession.getUsername());
            response.put("roles", userSession.getRoles());
            response.put("createdAt", userSession.getCreatedAt());
            response.put("lastAccessedAt", userSession.getLastAccessedAt());
            response.put("ipAddress", userSession.getIpAddress());

            return new GenericResponse<>(response);
        } catch (Exception e) {
            log.error("세션 조회 실패", e);
            return new GenericResponse<>(null, "세션 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 세션 삭제 (로그아웃)
     * @param request HTTP 요청
     * @return 삭제 결과
     */
    @DeleteMapping("/logout")
    public GenericResponse<String> logout(HttpServletRequest request) {
        try {
            HttpSession httpSession = request.getSession(false);

            if (httpSession != null) {
                String sessionId = httpSession.getId();
                userSessionService.deleteSession(sessionId);
                httpSession.invalidate();

                log.info("로그아웃: sessionId={}", sessionId);
                return new GenericResponse<>("로그아웃되었습니다.");
            }

            return new GenericResponse<>("세션이 존재하지 않습니다.");
        } catch (Exception e) {
            log.error("로그아웃 실패", e);
            return new GenericResponse<>(null, "로그아웃 중 오류가 발생했습니다.");
        }
    }

    /**
     * 세션 연장
     * @param request HTTP 요청
     * @return 연장 결과
     */
    @PostMapping("/extend")
    public GenericResponse<String> extendSession(HttpServletRequest request) {
        try {
            HttpSession httpSession = request.getSession(false);

            if (httpSession == null) {
                return new GenericResponse<>(null, "세션이 존재하지 않습니다.");
            }

            String sessionId = httpSession.getId();
            userSessionService.extendSession(sessionId);

            return new GenericResponse<>("세션이 연장되었습니다.");
        } catch (Exception e) {
            log.error("세션 연장 실패", e);
            return new GenericResponse<>(null, "세션 연장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     * @param request HTTP 요청
     * @return IP 주소
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
