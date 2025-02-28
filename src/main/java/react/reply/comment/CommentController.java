package react.reply.comment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import react.reply.user.UserEntity;
import react.reply.util.PageMaker;
import react.reply.util.PageVO;

@CrossOrigin(origins = {"http://localhost:3000/","http://localhost/"}) // CORS 허용 설정
@RestController // REST API 컨트롤러 선언
@RequestMapping("/api/comment") // "/api/comment" 경로로 요청 매핑
public class CommentController {

	@Autowired
	private CommentRepository commentRepo; // 댓글 레포지토리 주입

	// 댓글 목록 조회
	@GetMapping("/list")
	public PageMaker list(@RequestParam int parent_no, PageVO vo) {
		// 게시글 번호(parent_no)와 페이지 정보(vo)로 댓글 목록 조회
		return new PageMaker(commentRepo.findByParentno(parent_no, vo.makePageable(0, "no")));
	}

	// 댓글 등록
	@PostMapping("/regist")
	public Map<String, Object> regist(@RequestBody Map map) {
		UserEntity userEntity = new UserEntity();
		userEntity.setNo((Integer)map.get("user_no")); // 사용자 번호 설정

		CommentEntity commentEntity = new CommentEntity();
		commentEntity.setContent((String)map.get("content")); // 댓글 내용 설정
		commentEntity.setParentno((Integer)map.get("parent_no")); // 게시글 번호 설정
		commentEntity.setUser(userEntity); // 사용자 정보 설정

		CommentEntity entity = commentRepo.save(commentEntity); // 댓글 저장

		// 결과 반환
		Map<String, Object> result = new HashMap<>();
		result.put("entity", entity); // 저장된 엔티티
		result.put("result", entity == null ? "fail" : "success"); // 결과 상태
		return result;
	}

	// 댓글 삭제
	@GetMapping("/delete")
	public Map delete(@RequestParam int no, PageVO vo) {
		commentRepo.deleteById(no); // ID로 댓글 삭제

		// 결과 반환
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		return result;
	}

	// 게시글에 속한 모든 댓글 삭제
	@GetMapping("/deleteAll")
	public Map deleteAll(@RequestParam int parent_no, PageVO vo) {
		commentRepo.deleteByParentno(parent_no); // 게시글 번호로 모든 댓글 삭제

		// 결과 반환
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		return result;
	}
}