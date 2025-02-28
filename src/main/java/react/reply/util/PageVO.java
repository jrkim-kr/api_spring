/*
페이지네이션 관련 값 객체
 */
package react.reply.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageVO {
	private static final int DEFAULT_SIZE=10; // 기본 페이지 크기
	private static final int DEFAULT_MAX_SIZE=50; // 최대 페이지 크기

	private int page; // 페이지 번호
	private int size; // 페이지 크기
	private String searchWord; // 검색어
	private String searchType; // 검색 타입

	public PageVO() {
		this.page = 1; // 기본 페이지 번호
		this.size = DEFAULT_SIZE; // 기본 페이지 크기
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page < 0 ? 1 : page; // 페이지 번호가 0보다 작으면 1로 설정
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size < DEFAULT_SIZE || size > DEFAULT_MAX_SIZE ? DEFAULT_SIZE : size; // 페이지 크기가 범위를 벗어나면 기본값으로 설정
	}

	public Pageable makePageable() {
		// 정렬 기준: gno(그룹번호) 내림차순, ono(순서) 오름차순
		return PageRequest.of(this.page-1, this.size, Sort.by(Sort.Direction.DESC, "gno").and(Sort.by(Sort.Direction.ASC, "ono")));
	}

	public Pageable makePageable(int direction, String...props) {
		Sort.Direction dir = direction == 0 ? Sort.Direction.DESC : Sort.Direction.ASC; // 0이면 내림차순, 아니면 오름차순
		return PageRequest.of(this.page-1, this.size, dir, props); // Pageable 객체 생성
	}
}