package com.study.domain.post;

import com.study.common.dto.CommonDto;
import com.study.common.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostRestController {

  private final PostService postService;

  @PostMapping("/post/save.json")
  public CommonDto savePost(final PostRequest params) {
    postService.savePost(params);
    MessageDto message = new MessageDto("게시글 생성이 완료되었습니다.", "/post/list.do", RequestMethod.GET, null);
    return new CommonDto("true", message);
  }
}
