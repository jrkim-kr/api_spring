package react.reply.comment;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import react.reply.user.UserEntity;

@Getter
@Setter
@Entity
@Table(name="comment") // 'comment' 테이블과 매핑
@ToString
public class CommentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 식별자
	private int no; // 댓글 번호 (PK)

	@Column(name="parent_no") // 컬럼명 매핑
	private int parentno; // 게시글 번호 (FK)

	private String content; // 댓글 내용

	@CreationTimestamp // 생성 시 자동으로 시간 설정
	private Timestamp writedate; // 작성일시

	@ManyToOne // 사용자와의 N:1 관계 매핑
	@JoinColumn(name="user_no") // 외래 키 설정
	private UserEntity user; // 작성자 정보
}