/*
게시글 엔티티(데이터 모델)
 */
package react.reply.reply;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import react.reply.comment.CommentEntity;
import react.reply.user.UserEntity;

@Getter
@Setter
@Entity
@Table(name = "reply") // 'reply' 테이블과 매핑
@ToString
public class ReplyEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 식별자
	private int no; // 게시글 번호 (PK)
	private String title; // 게시글 제목
	private String content; // 게시글 내용
	@CreationTimestamp
	private Timestamp writedate; // 작성일시 (자동 생성)
	private int viewcnt; // 조회수
	private String filename_org; // 원본 파일명
	private String filename_real; // 저장된 실제 파일명
	private int likecnt; // 좋아요 수
	private int gno; // 그룹 번호 (원글은 자신의 번호, 답글은 원글의 번호)
	private int ono; // 그룹 내 순서 (원글은 0, 답글은 0보다 큰 값)
	private int nested; // 중첩 단계 (원글은 0, 답글은 0보다 큰 값)

	@OneToMany(mappedBy = "parentno") // 댓글과의 1:N 관계 매핑
	private List<CommentEntity> comment; // 댓글 목록

	@ManyToOne // 사용자와의 N:1 관계 매핑
	@JoinColumn(name="user_no") // 외래 키 설정
	private UserEntity user; // 작성자 정보
}