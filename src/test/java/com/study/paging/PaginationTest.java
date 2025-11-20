package com.study.paging;

import com.study.common.dto.SearchDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pagination 계산 테스트")
class PaginationTest {

    @Test
    @DisplayName("레코드가 0개일 때 페이지네이션 생성")
    void pagination_with_zero_records() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(0, params);

        // then
        assertThat(pagination.getTotalRecordCount()).isEqualTo(0);
        assertThat(pagination.getTotalPageCount()).isEqualTo(0);
        assertThat(pagination.getStartPage()).isEqualTo(0);
        assertThat(pagination.getEndPage()).isEqualTo(0);
        assertThat(pagination.getLimitStart()).isEqualTo(0);
        assertThat(pagination.isExistPrevPage()).isFalse();
        assertThat(pagination.isExistNextPage()).isFalse();
    }

    @Test
    @DisplayName("단일 페이지 (총 5개 레코드, 페이지당 10개)")
    void pagination_single_page() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(5, params);

        // then
        assertThat(pagination.getTotalRecordCount()).isEqualTo(5);
        assertThat(pagination.getTotalPageCount()).isEqualTo(1);
        assertThat(pagination.getStartPage()).isEqualTo(1);
        assertThat(pagination.getEndPage()).isEqualTo(1);
        assertThat(pagination.getLimitStart()).isEqualTo(0);
        assertThat(pagination.isExistPrevPage()).isFalse();
        assertThat(pagination.isExistNextPage()).isFalse();
    }

    @Test
    @DisplayName("다중 페이지 - 1페이지 조회 (총 25개 레코드, 페이지당 10개)")
    void pagination_first_page_of_multiple() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(25, params);

        // then
        assertThat(pagination.getTotalRecordCount()).isEqualTo(25);
        assertThat(pagination.getTotalPageCount()).isEqualTo(3);
        assertThat(pagination.getStartPage()).isEqualTo(1);
        assertThat(pagination.getEndPage()).isEqualTo(3);
        assertThat(pagination.getLimitStart()).isEqualTo(0);
        assertThat(pagination.isExistPrevPage()).isFalse();
        assertThat(pagination.isExistNextPage()).isTrue();
    }

    @Test
    @DisplayName("다중 페이지 - 2페이지 조회")
    void pagination_second_page() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(2);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(25, params);

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(3);
        assertThat(pagination.getStartPage()).isEqualTo(1);
        assertThat(pagination.getEndPage()).isEqualTo(3);
        assertThat(pagination.getLimitStart()).isEqualTo(10); // (2-1) * 10
        assertThat(pagination.isExistPrevPage()).isFalse();
        assertThat(pagination.isExistNextPage()).isTrue();
    }

    @Test
    @DisplayName("다중 페이지 - 마지막 페이지 조회")
    void pagination_last_page() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(3);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(25, params);

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(3);
        assertThat(pagination.getStartPage()).isEqualTo(1);
        assertThat(pagination.getEndPage()).isEqualTo(3);
        assertThat(pagination.getLimitStart()).isEqualTo(20); // (3-1) * 10
        assertThat(pagination.isExistPrevPage()).isFalse();
        assertThat(pagination.isExistNextPage()).isFalse(); // 마지막 페이지이므로 다음 없음
    }

    @Test
    @DisplayName("페이지 번호가 전체 페이지 수를 초과하는 경우")
    void pagination_page_overflow() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(999); // 존재하지 않는 페이지
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(25, params);

        // then - 자동으로 마지막 페이지로 조정됨
        assertThat(pagination.getTotalPageCount()).isEqualTo(3);
        assertThat(params.getPage()).isEqualTo(3); // 자동 조정
        assertThat(pagination.getLimitStart()).isEqualTo(20);
    }

    @Test
    @DisplayName("페이지 범위 계산 - 11페이지 조회 시 (pageSize=10)")
    void pagination_page_range_second_set() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(11); // 두 번째 페이지 세트
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(200, params); // 총 20페이지

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(20);
        assertThat(pagination.getStartPage()).isEqualTo(11); // 두 번째 세트 시작
        assertThat(pagination.getEndPage()).isEqualTo(20);
        assertThat(pagination.isExistPrevPage()).isTrue(); // 이전 페이지 존재
        assertThat(pagination.isExistNextPage()).isFalse();
    }

    @Test
    @DisplayName("페이지 범위 계산 - 15페이지 조회 시 (총 30페이지)")
    void pagination_page_range_middle() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(15);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(300, params); // 총 30페이지

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(30);
        assertThat(pagination.getStartPage()).isEqualTo(11);
        assertThat(pagination.getEndPage()).isEqualTo(20);
        assertThat(pagination.isExistPrevPage()).isTrue();
        assertThat(pagination.isExistNextPage()).isTrue();
    }

    @Test
    @DisplayName("정확히 10의 배수 레코드 (100개)")
    void pagination_exact_multiple_of_record_size() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(100, params);

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(10);
        assertThat(pagination.getStartPage()).isEqualTo(1);
        assertThat(pagination.getEndPage()).isEqualTo(10);
    }

    @Test
    @DisplayName("10의 배수 + 1 레코드 (101개)")
    void pagination_one_more_than_multiple() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(101, params);

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(11); // 한 페이지 추가
        assertThat(pagination.getEndPage()).isEqualTo(10); // 첫 번째 세트는 1-10
    }

    @ParameterizedTest(name = "총 {0}개 레코드, 페이지당 {1}개 -> 전체 {2}페이지")
    @CsvSource({
        "0, 10, 0",      // 레코드 없음
        "1, 10, 1",      // 단일 레코드
        "9, 10, 1",      // 한 페이지 미만
        "10, 10, 1",     // 정확히 한 페이지
        "11, 10, 2",     // 두 페이지
        "100, 10, 10",   // 정확히 10페이지
        "101, 10, 11",   // 11페이지
        "999, 10, 100",  // 대량 데이터
        "50, 20, 3",     // 다른 recordSize
    })
    @DisplayName("파라미터화 테스트 - 전체 페이지 수 계산")
    void pagination_total_page_count(int totalRecords, int recordSize, int expectedPages) {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(recordSize);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(totalRecords, params);

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(expectedPages);
    }

    @ParameterizedTest(name = "페이지 {0}, recordSize {1} -> LIMIT 시작 위치 {2}")
    @CsvSource({
        "1, 10, 0",      // 첫 페이지
        "2, 10, 10",     // 두 번째 페이지
        "3, 10, 20",     // 세 번째 페이지
        "10, 10, 90",    // 열 번째 페이지
        "1, 20, 0",      // 다른 recordSize
        "5, 20, 80",     // 다른 recordSize
    })
    @DisplayName("파라미터화 테스트 - LIMIT 시작 위치 계산")
    void pagination_limit_start_calculation(int page, int recordSize, int expectedLimitStart) {
        // given
        SearchDto params = new SearchDto();
        params.setPage(page);
        params.setRecordSize(recordSize);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(1000, params); // 충분한 레코드

        // then
        assertThat(pagination.getLimitStart()).isEqualTo(expectedLimitStart);
    }

    @Test
    @DisplayName("이전 페이지 존재 여부 - startPage가 1이 아닐 때")
    void pagination_exist_prev_page_true() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(11);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(300, params);

        // then
        assertThat(pagination.getStartPage()).isEqualTo(11);
        assertThat(pagination.isExistPrevPage()).isTrue();
    }

    @Test
    @DisplayName("다음 페이지 존재 여부 - endPage * recordSize < totalRecordCount")
    void pagination_exist_next_page_true() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(10);

        // when
        Pagination pagination = new Pagination(150, params);

        // then
        assertThat(pagination.getEndPage()).isEqualTo(10);
        assertThat(pagination.isExistNextPage()).isTrue(); // 10 * 10 = 100 < 150
    }

    @Test
    @DisplayName("커스텀 pageSize - 페이지 링크 5개만 표시")
    void pagination_custom_page_size() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(1);
        params.setRecordSize(10);
        params.setPageSize(5); // 페이지 링크 5개만

        // when
        Pagination pagination = new Pagination(100, params);

        // then
        assertThat(pagination.getTotalPageCount()).isEqualTo(10);
        assertThat(pagination.getStartPage()).isEqualTo(1);
        assertThat(pagination.getEndPage()).isEqualTo(5); // pageSize만큼만
    }

    @Test
    @DisplayName("커스텀 pageSize - 6페이지 조회 시 두 번째 세트")
    void pagination_custom_page_size_second_set() {
        // given
        SearchDto params = new SearchDto();
        params.setPage(6);
        params.setRecordSize(10);
        params.setPageSize(5);

        // when
        Pagination pagination = new Pagination(100, params);

        // then
        assertThat(pagination.getStartPage()).isEqualTo(6);
        assertThat(pagination.getEndPage()).isEqualTo(10);
        assertThat(pagination.isExistPrevPage()).isTrue();
    }
}
