package com.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.domain.post.PostRequest;
import com.study.domain.post.PostResponse;
import com.study.domain.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    void save() {
        PostRequest params = new PostRequest();
        params.setTitle("1번 게시글 제목");
        params.setContent("1번 게시글 내용");
        params.setWriter("테스터");
        params.setNoticeYn(false);
        Long id = postService.savePost(params);
        System.out.println("생성된 게시글 ID : " + id);
    }

    @Test
    void findPostById() {
        PostResponse post = postService.findPostById(1L);
        try {
            String postJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(post);
            System.out.println(postJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
