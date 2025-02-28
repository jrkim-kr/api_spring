/*
페이지네이션 UI를 위한 데이터 생성 클래스
 */
package react.reply.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

@Getter
@ToString(exclude="pageList")
@Log
public class PageMaker<T> {
	private Page<T> result; // 페이지 결과
	private Pageable prevPage; // 이전 페이지
	private Pageable nextPage; // 다음 페이지
	private int currentPageNum; // 현재 페이지 번호
	private int totalPageNum; // 전체 페이지 수
	private Pageable currentPage; // 현재 페이지 객체
	private List<Pageable> pageList; // 페이지 번호 목록

	public PageMaker(Page<T> result) {
		this.result = result;
		this.currentPage = result.getPageable();
		this.currentPageNum = currentPage.getPageNumber()+1; // 0부터 시작하므로 +1
		this.totalPageNum = result.getTotalPages();
		this.pageList = new ArrayList();
		calcPages(); // 페이지 번호 목록 계산
	}

	private void calcPages() {
		int tempEndNum = (int)(Math.ceil(this.currentPageNum/10.0)*10); // 현재 페이지 그룹의 마지막 페이지 번호 계산
		int startNum = tempEndNum - 9; // 현재 페이지 그룹의 첫 페이지 번호 계산
		Pageable startPage = this.currentPage;

		// 시작 페이지로 이동
		for (int i=startNum; i<this.currentPageNum; i++) {
			startPage = startPage.previousOrFirst();
		}

		// 이전 페이지 그룹 설정
		this.prevPage = startPage.getPageNumber() <= 0 ? null : startPage.previousOrFirst();

		// 전체 페이지 수가 계산된 마지막 페이지보다 작으면 마지막 페이지 조정
		if (this.totalPageNum < tempEndNum) {
			tempEndNum = this.totalPageNum;
			this.nextPage = null; // 다음 페이지 그룹 없음
		}

		// 페이지 목록 생성
		for (int i=startNum; i<=tempEndNum; i++) {
			pageList.add(startPage);
			startPage = startPage.next();
			// 다음 페이지 그룹 설정
			this.nextPage = startPage.getPageNumber()+1 < totalPageNum ? startPage : null;
		}
	}
}