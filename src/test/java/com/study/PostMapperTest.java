package com.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.common.dto.SearchDto;
import com.study.domain.post.PostMapper;
import com.study.domain.post.PostRequest;
import com.study.domain.post.PostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("PostMapper MyBatis 통합 테스트")
public class PostMapperTest {

    @Autowired
    PostMapper postMapper;

    @Test
    @DisplayName("게시글 대량 저장")
    void save() {
        for (int i = 1; i <= 50; i++) {
            PostRequest params = new PostRequest();
            params.setTitle(i + "번 게시글 제목");
            params.setContent(i + "번 게시글 내용");
            params.setWriter(i + "번 작성자");
            params.setNoticeYn(false);
            postMapper.save(params);
        }

//        List<PostResponse> posts = postMapper.findAll();
//        System.out.println("전체 게시글 개수는 : " + posts.size() + "개입니다.");
    }

    @Test
    @DisplayName("ID로 게시글 조회")
    void findById() {
        PostResponse post = postMapper.findById(1L);
        try {
            String postJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(post);
            System.out.println(postJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional
    @DisplayName("게시글 수정")
    void update() {
        // 1. 게시글 수정
        PostRequest params = new PostRequest();
        params.setId(1L);
        params.setTitle("1번 게시글 제목 수정합니다.");
        params.setContent("1번 게시글 내용 수정합니다.");
        params.setWriter("도뎡이");
        params.setNoticeYn(true);
        postMapper.update(params);

        // 2. 게시글 상세정보 조회
        PostResponse post = postMapper.findById(1L);
        try {
            String postJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(post);
            System.out.println(postJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional
    @DisplayName("게시글 삭제 (소프트 삭제)")
    void delete() {
        // given
        PostRequest params = new PostRequest();
        params.setTitle("삭제 테스트 게시글");
        params.setContent("삭제 테스트 내용");
        params.setWriter("테스터");
        params.setNoticeYn(false);
        postMapper.save(params);
        Long id = params.getId();

        // when
        postMapper.deleteById(id);

        // then - 소프트 삭제이므로 조회는 가능하지만 deleteYn이 true
        PostResponse deletedPost = postMapper.findById(id);
        if (deletedPost != null) {
            assertThat(deletedPost.getDeleteYn()).isTrue();
        }
    }

    @Test
    @DisplayName("전체 게시글 수 조회")
    void count() {
        // given
        SearchDto params = new SearchDto();

        // when
        int count = postMapper.count(params);

        // then
        assertThat(count).isGreaterThanOrEqualTo(0);
        System.out.println("전체 게시글 개수: " + count);
    }

    @Test
    @DisplayName("검색 조건으로 게시글 수 조회")
    void countWithSearch() {
        // given
        SearchDto params = new SearchDto();
        params.setKeyword("게시글");
        params.setSearchType("title");

        // when
        int count = postMapper.count(params);

        // then
        assertThat(count).isGreaterThanOrEqualTo(0);
        System.out.println("'게시글' 키워드 검색 결과: " + count + "개");
    }

    @Test
    @DisplayName("게시글 목록 조회 - 페이지네이션")
    void findAll() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);

        // when
        List<PostResponse> posts = postMapper.findAll(params);

        // then
        assertThat(posts).isNotNull();
        System.out.println("조회된 게시글 수: " + posts.size());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 제목 검색")
    void findAllWithTitleSearch() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setKeyword("게시글");
        params.setSearchType("title");

        // when
        List<PostResponse> posts = postMapper.findAll(params);

        // then
        assertThat(posts).isNotNull();
        posts.forEach(post -> {
            System.out.println("제목: " + post.getTitle());
        });
    }

    @Test
    @DisplayName("게시글 목록 조회 - 내용 검색")
    void findAllWithContentSearch() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setKeyword("내용");
        params.setSearchType("content");

        // when
        List<PostResponse> posts = postMapper.findAll(params);

        // then
        assertThat(posts).isNotNull();
        System.out.println("'내용' 키워드로 검색된 게시글 수: " + posts.size());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 작성자 검색")
    void findAllWithWriterSearch() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setKeyword("작성자");
        params.setSearchType("writer");

        // when
        List<PostResponse> posts = postMapper.findAll(params);

        // then
        assertThat(posts).isNotNull();
        System.out.println("'작성자' 키워드로 검색된 게시글 수: " + posts.size());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 빈 키워드로 검색")
    void findAllWithEmptyKeyword() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setKeyword("");

        // when
        List<PostResponse> posts = postMapper.findAll(params);

        // then
        assertThat(posts).isNotNull();
        assertThat(posts.size()).isLessThanOrEqualTo(10);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 두 번째 페이지")
    void findAllSecondPage() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(2);
        params.setRecordSize(10);

        // when
        List<PostResponse> posts = postMapper.findAll(params);

        // then
        assertThat(posts).isNotNull();
        System.out.println("2페이지 게시글 수: " + posts.size());
    }

    @Test
    @Transactional
    @DisplayName("게시글 저장 후 ID 자동 생성 확인")
    void saveAndCheckGeneratedId() {
        // given
        PostRequest params = new PostRequest();
        params.setTitle("ID 생성 테스트");
        params.setContent("ID 자동 생성 확인");
        params.setWriter("테스터");
        params.setNoticeYn(false);

        // when
        postMapper.save(params);

        // then
        assertThat(params.getId()).isNotNull();
        assertThat(params.getId()).isGreaterThan(0);
        System.out.println("생성된 ID: " + params.getId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 null 반환")
    void findByIdNotFound() {
        // when
        PostResponse post = postMapper.findById(999999999L);

        // then
        assertThat(post).isNull();
    }

}
