/*
게시글 데이터 접근 인터페이스
*/
package react.reply.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import react.reply.reply.ReplyEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {
	// 제목 검색 메소드
	public Page<ReplyEntity> findByTitleContaining(String searchWord, Pageable page);

	// 내용 검색 메소드
	public Page<ReplyEntity> findByContentContaining(String searchWord, Pageable page);

	// 제목 또는 내용 검색 메소드 (OR 조건)
	public Page<ReplyEntity> findByTitleContainingOrContentContaining(String searchWord, String searchWord2, Pageable page);

	// 답변글 순서 업데이트 (답변글 삽입 시 사용)
	@Modifying // update 쿼리 사용 시 필요한 어노테이션
	@Query("update ReplyEntity set ono=ono+1 where gno=?1 AND ono > ?2")
	public void updateOno(int gno, int ono);
}
