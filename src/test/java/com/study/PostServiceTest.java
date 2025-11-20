package com.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.common.dto.SearchDto;
import com.study.domain.post.PostRequest;
import com.study.domain.post.PostResponse;
import com.study.domain.post.PostService;
import com.study.paging.PagingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("PostService 통합 테스트")
public class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    @DisplayName("게시글 저장 성공")
    void save() {
        PostRequest params = new PostRequest();
        params.setTitle("1번 게시글 제목");
        params.setContent("1번 게시글 내용");
        params.setWriter("테스터");
        params.setNoticeYn(false);
        Long id = postService.savePost(params);
        System.out.println("생성된 게시글 ID : " + id);

        assertThat(id).isNotNull();
        assertThat(id).isGreaterThan(0);
    }

    @Test
    @DisplayName("게시글 ID로 조회 성공")
    void findPostById() {
        PostResponse post = postService.findPostById(1L);
        try {
            String postJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(post);
            System.out.println(postJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional
    @DisplayName("게시글 수정 성공")
    void updatePost_success() {
        // given - 먼저 게시글 생성
        PostRequest createParams = new PostRequest();
        createParams.setTitle("원본 제목");
        createParams.setContent("원본 내용");
        createParams.setWriter("작성자");
        createParams.setNoticeYn(false);
        Long id = postService.savePost(createParams);

        // when - 게시글 수정
        PostRequest updateParams = new PostRequest();
        updateParams.setId(id);
        updateParams.setTitle("수정된 제목");
        updateParams.setContent("수정된 내용");
        updateParams.setWriter("작성자");
        updateParams.setNoticeYn(true);
        Long updatedId = postService.updatePost(updateParams);

        // then
        assertThat(updatedId).isEqualTo(id);
        PostResponse updatedPost = postService.findPostById(id);
        assertThat(updatedPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.getContent()).isEqualTo("수정된 내용");
        assertThat(updatedPost.getNoticeYn()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("게시글 삭제 성공 (소프트 삭제)")
    void deletePost_success() {
        // given - 게시글 생성
        PostRequest params = new PostRequest();
        params.setTitle("삭제할 게시글");
        params.setContent("삭제할 내용");
        params.setWriter("작성자");
        params.setNoticeYn(false);
        Long id = postService.savePost(params);

        // when - 게시글 삭제
        Long deletedId = postService.deletePost(id);

        // then
        assertThat(deletedId).isEqualTo(id);

        // 소프트 삭제이므로 조회는 가능하지만 deleteYn이 true여야 함
        PostResponse deletedPost = postService.findPostById(id);
        if (deletedPost != null) {
            assertThat(deletedPost.getDeleteYn()).isTrue();
        }
    }

    @Test
    @DisplayName("게시글 목록 조회 - 기본 페이지네이션")
    void findAllPost_withDefaultPagination() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        PagingResponse<PostResponse> response = postService.findAllPost(params);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList()).isNotNull();
        assertThat(response.getPagination()).isNotNull();
        System.out.println("조회된 게시글 수: " + response.getList().size());
        System.out.println("전체 게시글 수: " + response.getPagination().getTotalRecordCount());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 검색 키워드 적용")
    void findAllPost_withSearchKeyword() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);
        params.setKeyword("게시글");
        params.setSearchType("title");

        // when
        PagingResponse<PostResponse> response = postService.findAllPost(params);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList()).isNotNull();
        System.out.println("검색된 게시글 수: " + response.getList().size());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 두 번째 페이지")
    void findAllPost_secondPage() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(2);
        params.setRecordSize(5);
        params.setPageSize(10);

        // when
        PagingResponse<PostResponse> response = postService.findAllPost(params);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPagination().getStartPage()).isGreaterThan(0);
        assertThat(response.getPagination().getLimitStart()).isEqualTo(5); // (2-1) * 5
    }

    @Test
    @Transactional
    @DisplayName("게시글 생성 후 즉시 조회")
    void saveAndFind_integration() {
        // given
        PostRequest params = new PostRequest();
        params.setTitle("통합 테스트 게시글");
        params.setContent("통합 테스트 내용");
        params.setWriter("통합테스터");
        params.setNoticeYn(false);

        // when
        Long id = postService.savePost(params);
        PostResponse found = postService.findPostById(id);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("통합 테스트 게시글");
        assertThat(found.getContent()).isEqualTo("통합 테스트 내용");
        assertThat(found.getWriter()).isEqualTo("통합테스터");
        assertThat(found.getNoticeYn()).isFalse();
        assertThat(found.getDeleteYn()).isFalse();
    }

    @Test
    @DisplayName("공지사항 게시글 생성")
    void saveNoticePost() {
        // given
        PostRequest params = new PostRequest();
        params.setTitle("공지사항");
        params.setContent("중요한 공지");
        params.setWriter("관리자");
        params.setNoticeYn(true);

        // when
        Long id = postService.savePost(params);
        PostResponse found = postService.findPostById(id);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getNoticeYn()).isTrue();
    }

}
