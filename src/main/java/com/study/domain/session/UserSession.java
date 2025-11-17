package com.study.domain.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 사용자 세션 정보
 * Redis에 저장될 세션 데이터
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 사용자 고유 키
     */
    private Long userKey;

    /**
     * 사용자 이름
     */
    private String username;

    /**
     * 사용자 권한 목록
     */
    private String[] roles;

    /**
     * 세션 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 마지막 접근 시간
     */
    private LocalDateTime lastAccessedAt;

    /**
     * 사용자 IP 주소
     */
    private String ipAddress;

    /**
     * 사용자 에이전트 (브라우저 정보)
     */
    private String userAgent;

    /**
     * 추가 메타데이터 (JSON 형식)
     */
    private String metadata;
}
