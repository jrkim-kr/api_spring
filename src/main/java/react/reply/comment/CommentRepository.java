package react.reply.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
	// 게시글 번호로 댓글 목록 조회
	public Page<CommentEntity> findByParentno(int parent_no, Pageable page);

	// 게시글 번호로 모든 댓글 삭제
	public void deleteByParentno(int parent_no);
}