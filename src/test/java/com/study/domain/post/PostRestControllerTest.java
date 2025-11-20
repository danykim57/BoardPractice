package com.study.domain.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostRestController.class)
@DisplayName("PostRestController REST API 테스트")
class PostRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /post/save.json - 게시글 저장 성공")
    void savePost_success() throws Exception {
        // given
        Long expectedId = 123L;
        when(postService.savePost(any(PostRequest.class))).thenReturn(expectedId);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "테스트 제목")
                .param("content", "테스트 내용")
                .param("writer", "테스터")
                .param("noticeYn", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("true"))
            .andExpect(jsonPath("$.data.message").value("게시글 생성이 완료되었습니다."))
            .andExpect(jsonPath("$.data.redirectUrl").value("/post/list.do"));
    }

    @Test
    @DisplayName("POST /post/save.json - 공지사항 게시글 저장")
    void saveNoticePost_success() throws Exception {
        // given
        Long expectedId = 456L;
        when(postService.savePost(any(PostRequest.class))).thenReturn(expectedId);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "공지사항")
                .param("content", "중요한 공지")
                .param("writer", "관리자")
                .param("noticeYn", "true")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    @DisplayName("POST /post/save.json - 빈 제목으로 요청")
    void savePost_withEmptyTitle() throws Exception {
        // given
        when(postService.savePost(any(PostRequest.class))).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "")
                .param("content", "내용")
                .param("writer", "작성자")
                .param("noticeYn", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /post/save.json - 모든 필드 제공")
    void savePost_withAllFields() throws Exception {
        // given
        when(postService.savePost(any(PostRequest.class))).thenReturn(789L);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "완전한 게시글")
                .param("content", "모든 필드가 있는 내용")
                .param("writer", "작성자명")
                .param("noticeYn", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").exists())
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("POST /post/save.json - noticeYn이 true인 경우")
    void savePost_noticeYnTrue() throws Exception {
        // given
        when(postService.savePost(any(PostRequest.class))).thenReturn(100L);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "공지")
                .param("content", "공지 내용")
                .param("writer", "관리자")
                .param("noticeYn", "true")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    @DisplayName("POST /post/save.json - noticeYn이 false인 경우")
    void savePost_noticeYnFalse() throws Exception {
        // given
        when(postService.savePost(any(PostRequest.class))).thenReturn(200L);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "일반 게시글")
                .param("content", "일반 내용")
                .param("writer", "사용자")
                .param("noticeYn", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    @DisplayName("POST /post/save.json - 긴 제목과 내용")
    void savePost_withLongContent() throws Exception {
        // given
        when(postService.savePost(any(PostRequest.class))).thenReturn(300L);
        String longTitle = "매우 ".repeat(100) + "긴 제목";
        String longContent = "매우 ".repeat(1000) + "긴 내용";

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", longTitle)
                .param("content", longContent)
                .param("writer", "작성자")
                .param("noticeYn", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /post/save.json - 특수문자 포함")
    void savePost_withSpecialCharacters() throws Exception {
        // given
        when(postService.savePost(any(PostRequest.class))).thenReturn(400L);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "특수문자 테스트!@#$%^&*()")
                .param("content", "내용에도 특수문자 <>&\"'")
                .param("writer", "작성자123")
                .param("noticeYn", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    @DisplayName("POST /post/save.json - 한글 제목과 내용")
    void savePost_withKoreanCharacters() throws Exception {
        // given
        when(postService.savePost(any(PostRequest.class))).thenReturn(500L);

        // when & then
        mockMvc.perform(post("/post/save.json")
                .param("title", "한글 제목입니다")
                .param("content", "한글 내용입니다. 완전한 문장으로 작성했습니다.")
                .param("writer", "김철수")
                .param("noticeYn", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("true"));
    }
}
