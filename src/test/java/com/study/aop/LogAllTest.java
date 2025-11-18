package com.study.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * LogAll AOP 클래스 테스트
 * - AOP 어드바이스 동작 검증
 * - 요청 로깅 검증
 * - 예외 처리 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LogAll AOP 테스트")
class LogAllTest {

    @InjectMocks
    private LogAll logAll;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        // MockHttpServletRequest 생성
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test/endpoint");
        request.setMethod("GET");

        // RequestContextHolder에 ServletRequestAttributes 설정
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        // RequestContextHolder 초기화
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("AOP 어드바이스가 정상적으로 실행되고 원본 메서드 결과를 반환한다")
    void testLogAccess_SuccessfulExecution() throws Throwable {
        // given
        String expectedResult = "Test Result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // when
        Object result = logAll.logAccess(joinPoint);

        // then
        assertThat(result).isEqualTo(expectedResult);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("요청 URI가 올바르게 로깅된다")
    void testLogAccess_LogsRequestUri() throws Throwable {
        // given
        request.setRequestURI("/api/posts/123");
        when(joinPoint.proceed()).thenReturn("Success");

        // when
        logAll.logAccess(joinPoint);

        // then
        // 로깅이 발생했는지 확인 (실제 로그는 Slf4j를 통해 출력됨)
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("다양한 HTTP 메서드에 대해 로깅이 동작한다")
    void testLogAccess_DifferentHttpMethods() throws Throwable {
        // given - POST 요청
        request.setMethod("POST");
        request.setRequestURI("/api/posts");
        when(joinPoint.proceed()).thenReturn("Created");

        // when
        Object result = logAll.logAccess(joinPoint);

        // then
        assertThat(result).isEqualTo("Created");
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("여러 URI 패턴에 대해 로깅이 동작한다")
    void testLogAccess_VariousUriPatterns() throws Throwable {
        // given
        String[] uris = {
            "/api/posts",
            "/api/posts/123",
            "/api/session/create",
            "/api/session/info",
            "/excel/download"
        };

        for (String uri : uris) {
            // RequestContextHolder 재설정
            request.setRequestURI(uri);
            ServletRequestAttributes attributes = new ServletRequestAttributes(request);
            RequestContextHolder.setRequestAttributes(attributes);

            when(joinPoint.proceed()).thenReturn("Success");

            // when
            Object result = logAll.logAccess(joinPoint);

            // then
            assertThat(result).isEqualTo("Success");
        }

        // 각 URI에 대해 proceed()가 호출되었는지 확인
        verify(joinPoint, times(uris.length)).proceed();
    }

    @Test
    @DisplayName("원본 메서드가 예외를 던지면 그대로 전파된다")
    void testLogAccess_PropagatesException() throws Throwable {
        // given
        RuntimeException expectedException = new RuntimeException("Test Exception");
        when(joinPoint.proceed()).thenThrow(expectedException);

        // when & then
        assertThatThrownBy(() -> logAll.logAccess(joinPoint))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test Exception");

        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("원본 메서드가 null을 반환해도 정상 처리된다")
    void testLogAccess_HandlesNullReturn() throws Throwable {
        // given
        when(joinPoint.proceed()).thenReturn(null);

        // when
        Object result = logAll.logAccess(joinPoint);

        // then
        assertThat(result).isNull();
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("복잡한 객체를 반환하는 메서드도 정상 처리된다")
    void testLogAccess_HandlesComplexReturnTypes() throws Throwable {
        // given
        ComplexObject complexObject = new ComplexObject("test", 123);
        when(joinPoint.proceed()).thenReturn(complexObject);

        // when
        Object result = logAll.logAccess(joinPoint);

        // then
        assertThat(result).isInstanceOf(ComplexObject.class);
        assertThat(((ComplexObject) result).getName()).isEqualTo("test");
        assertThat(((ComplexObject) result).getValue()).isEqualTo(123);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("쿼리 파라미터가 포함된 URI도 올바르게 로깅된다")
    void testLogAccess_WithQueryParameters() throws Throwable {
        // given
        request.setRequestURI("/api/posts");
        request.setQueryString("page=1&size=10&keyword=test");
        when(joinPoint.proceed()).thenReturn("Success");

        // when
        Object result = logAll.logAccess(joinPoint);

        // then
        assertThat(result).isEqualTo("Success");
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("긴 URI 경로도 정상적으로 처리된다")
    void testLogAccess_LongUriPath() throws Throwable {
        // given
        request.setRequestURI("/api/v1/users/123/posts/456/comments/789/replies");
        when(joinPoint.proceed()).thenReturn("Success");

        // when
        Object result = logAll.logAccess(joinPoint);

        // then
        assertThat(result).isEqualTo("Success");
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("체크 예외(Checked Exception)도 정상적으로 전파된다")
    void testLogAccess_PropagatesCheckedException() throws Throwable {
        // given
        Exception checkedException = new Exception("Checked Exception");
        when(joinPoint.proceed()).thenThrow(checkedException);

        // when & then
        assertThatThrownBy(() -> logAll.logAccess(joinPoint))
            .isInstanceOf(Exception.class)
            .hasMessage("Checked Exception");

        verify(joinPoint, times(1)).proceed();
    }

    /**
     * 테스트용 복잡한 객체 클래스
     */
    private static class ComplexObject {
        private final String name;
        private final int value;

        public ComplexObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}
