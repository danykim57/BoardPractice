package com.study.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SearchDto 테스트")
class SearchDtoTest {

    @Test
    @DisplayName("기본 생성자 - 기본값 설정 확인")
    void default_constructor_sets_default_values() {
        // when
        SearchDto searchDto = new SearchDto();

        // then
        assertThat(searchDto.getPage()).isEqualTo(1);
        assertThat(searchDto.getRecordSize()).isEqualTo(10);
        assertThat(searchDto.getPageSize()).isEqualTo(10);
        assertThat(searchDto.getKeyword()).isNull();
        assertThat(searchDto.getSearchType()).isNull();
        assertThat(searchDto.getPagination()).isNull();
    }

    @Test
    @DisplayName("첫 페이지 offset 계산 - 0이어야 함")
    void first_page_offset_should_be_zero() {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(1);
        searchDto.setRecordSize(10);

        // when
        int offset = searchDto.getOffset();

        // then
        assertThat(offset).isEqualTo(0); // (1-1) * 10 = 0
    }

    @Test
    @DisplayName("두 번째 페이지 offset 계산")
    void second_page_offset() {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(2);
        searchDto.setRecordSize(10);

        // when
        int offset = searchDto.getOffset();

        // then
        assertThat(offset).isEqualTo(10); // (2-1) * 10 = 10
    }

    @Test
    @DisplayName("열 번째 페이지 offset 계산")
    void tenth_page_offset() {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(10);
        searchDto.setRecordSize(10);

        // when
        int offset = searchDto.getOffset();

        // then
        assertThat(offset).isEqualTo(90); // (10-1) * 10 = 90
    }

    @ParameterizedTest(name = "페이지 {0}, recordSize {1} -> offset {2}")
    @CsvSource({
        "1, 10, 0",      // 첫 페이지
        "2, 10, 10",     // 두 번째 페이지
        "3, 10, 20",     // 세 번째 페이지
        "5, 10, 40",     // 다섯 번째 페이지
        "10, 10, 90",    // 열 번째 페이지
        "1, 20, 0",      // 다른 recordSize - 첫 페이지
        "2, 20, 20",     // 다른 recordSize - 두 번째 페이지
        "5, 20, 80",     // 다른 recordSize
        "1, 5, 0",       // 작은 recordSize
        "10, 5, 45",     // 작은 recordSize
        "100, 10, 990",  // 큰 페이지 번호
    })
    @DisplayName("파라미터화 테스트 - offset 계산")
    void offset_calculation_parameterized(int page, int recordSize, int expectedOffset) {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(page);
        searchDto.setRecordSize(recordSize);

        // when
        int offset = searchDto.getOffset();

        // then
        assertThat(offset).isEqualTo(expectedOffset);
    }

    @Test
    @DisplayName("검색 키워드 설정")
    void set_keyword() {
        // given
        SearchDto searchDto = new SearchDto();

        // when
        searchDto.setKeyword("테스트");

        // then
        assertThat(searchDto.getKeyword()).isEqualTo("테스트");
    }

    @Test
    @DisplayName("검색 타입 설정")
    void set_search_type() {
        // given
        SearchDto searchDto = new SearchDto();

        // when
        searchDto.setSearchType("title");

        // then
        assertThat(searchDto.getSearchType()).isEqualTo("title");
    }

    @Test
    @DisplayName("페이지 번호 변경 후 offset 재계산")
    void offset_recalculates_after_page_change() {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(1);
        searchDto.setRecordSize(10);

        // when
        int firstOffset = searchDto.getOffset();
        searchDto.setPage(3);
        int secondOffset = searchDto.getOffset();

        // then
        assertThat(firstOffset).isEqualTo(0);
        assertThat(secondOffset).isEqualTo(20);
    }

    @Test
    @DisplayName("recordSize 변경 후 offset 재계산")
    void offset_recalculates_after_record_size_change() {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(2);
        searchDto.setRecordSize(10);

        // when
        int firstOffset = searchDto.getOffset();
        searchDto.setRecordSize(20);
        int secondOffset = searchDto.getOffset();

        // then
        assertThat(firstOffset).isEqualTo(10); // (2-1) * 10
        assertThat(secondOffset).isEqualTo(20); // (2-1) * 20
    }

    @Test
    @DisplayName("모든 필드 설정 확인")
    void set_all_fields() {
        // given
        SearchDto searchDto = new SearchDto();

        // when
        searchDto.setPage(5);
        searchDto.setRecordSize(20);
        searchDto.setPageSize(15);
        searchDto.setKeyword("검색어");
        searchDto.setSearchType("content");

        // then
        assertThat(searchDto.getPage()).isEqualTo(5);
        assertThat(searchDto.getRecordSize()).isEqualTo(20);
        assertThat(searchDto.getPageSize()).isEqualTo(15);
        assertThat(searchDto.getKeyword()).isEqualTo("검색어");
        assertThat(searchDto.getSearchType()).isEqualTo("content");
        assertThat(searchDto.getOffset()).isEqualTo(80); // (5-1) * 20
    }

    @Test
    @DisplayName("빈 문자열 키워드 설정")
    void set_empty_keyword() {
        // given
        SearchDto searchDto = new SearchDto();

        // when
        searchDto.setKeyword("");

        // then
        assertThat(searchDto.getKeyword()).isEmpty();
    }

    @Test
    @DisplayName("페이지 번호가 0일 때 offset - 음수 발생")
    void page_zero_produces_negative_offset() {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(0);
        searchDto.setRecordSize(10);

        // when
        int offset = searchDto.getOffset();

        // then
        // 참고: 실제 애플리케이션에서는 page가 1 미만일 수 없도록 검증해야 함
        assertThat(offset).isEqualTo(-10); // (0-1) * 10 = -10
    }

    @Test
    @DisplayName("커스텀 recordSize로 페이지네이션")
    void custom_record_size() {
        // given
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(1);
        searchDto.setRecordSize(25);

        // when & then
        assertThat(searchDto.getRecordSize()).isEqualTo(25);
        assertThat(searchDto.getOffset()).isEqualTo(0);

        // when
        searchDto.setPage(3);

        // then
        assertThat(searchDto.getOffset()).isEqualTo(50); // (3-1) * 25
    }
}
