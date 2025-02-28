/*
게시글 목록 조회 API 엔드포인트
 */
package react.reply.reply;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import react.reply.comment.CommentRepository;
import react.reply.user.UserEntity;
import react.reply.util.PageMaker;
import react.reply.util.PageVO;

@CrossOrigin(origins = {"http://localhost:3000/","http://localhost/"}) // CORS 허용 설정
@RestController // REST API 컨트롤러 선언
@RequestMapping("/api/reply") // "/api/reply" 경로로 요청 매핑
public class ReplyController {
	@Autowired
	private ReplyRepository replyRepo; // 게시글 레포지토리 주입

	@GetMapping("/list") // GET 요청 처리
	public PageMaker list(PageVO vo) { // PageVO: 페이지네이션, 검색 관련 파라미터
		Page<ReplyEntity> page = null;
		// 검색 조건에 따른 분기 처리
		if ("all".equals(vo.getSearchType())) {
			// 제목+내용 검색
			page = replyRepo.findByTitleContainingOrContentContaining(vo.getSearchWord(), vo.getSearchWord(), vo.makePageable());
		} else if ("title".equals(vo.getSearchType())) {
			// 제목 검색
			page = replyRepo.findByTitleContaining(vo.getSearchWord(), vo.makePageable());
		} else if ("content".equals(vo.getSearchType())) {
			// 내용 검색
			page = replyRepo.findByContentContaining(vo.getSearchWord(), vo.makePageable());
		} else {
			// 검색 없는 전체 목록
			page = replyRepo.findAll(vo.makePageable());
		}
		return new PageMaker(page); // PageMaker 객체로 변환하여 반환
	}
	
	@Transactional
	@PostMapping("/regist")
	public Map<String, Object> regist(@RequestParam Map map, @RequestParam(name = "file", required = false) MultipartFile file) {
		UserEntity userEntity = new UserEntity();
		userEntity.setNo(Integer.parseInt((String)map.get("user_no")));
		
		ReplyEntity replyEntity = new ReplyEntity();
		replyEntity.setTitle((String)map.get("title"));
		replyEntity.setContent((String)map.get("content"));
		
		// 첨부파일
		if (file != null && !file.isEmpty()) {
			// 파일명
			String org = file.getOriginalFilename();
			String ext = org.substring(org.lastIndexOf("."));
			String real = System.currentTimeMillis()+ext;
			System.out.println(org);
			System.out.println(real);
			// 파일저장
			String path = "/Users/jrkim/Java/upload/"+real;
			try {
				file.transferTo(new File(path));
			} catch (Exception e) {}
			replyEntity.setFilename_org(org);
			replyEntity.setFilename_real(real);
		}
		
		
		replyEntity.setUser(userEntity);
		ReplyEntity entity = replyRepo.save(replyEntity);
		if (entity != null) {
			entity.setGno(entity.getNo());
			replyRepo.save(entity);
		}
		Map<String, Object> result = new HashMap<>();
		result.put("entity", entity);
		result.put("result", entity == null ? "fail" : "success");
		return result;
	}
	
	@GetMapping("/view")
	public ReplyEntity view(int no, PageVO vo) {
		ReplyEntity entity = replyRepo.findById(no).get();
		entity.setViewcnt(entity.getViewcnt()+1);
		replyRepo.save(entity);
		return entity;
	}
	
	@Autowired
	private CommentRepository commentRepo;
	
	@Transactional // 안넣으면 댓글삭제가 안됨
	@PostMapping("/delete")
	public Map<String, Object> delete(@RequestBody Map map) {
		replyRepo.deleteById((Integer)map.get("no"));
		commentRepo.deleteByParentno((Integer)map.get("no"));
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		return result;
	}
	
	@GetMapping("/edit")
	public ReplyEntity edit(int no, PageVO vo) {
		ReplyEntity entity = replyRepo.findById(no).get();
		return entity;
	}
	
	@Transactional
	@PostMapping("/update")
	public Map<String, Object> update(@RequestParam Map map, @RequestParam(name = "file", required = false) MultipartFile file) {
		
		ReplyEntity replyEntity = replyRepo.findById(Integer.parseInt((String)map.get("no"))).get();
		replyEntity.setTitle((String)map.get("title"));
		replyEntity.setContent((String)map.get("content"));
		if ("ok".equals((String)map.get("fileDel"))) {
			replyEntity.setFilename_org(null);
			replyEntity.setFilename_real(null);
		}
		
		// 첨부파일
		if (file != null && !file.isEmpty()) {
			// 파일명
			String org = file.getOriginalFilename();
			String ext = org.substring(org.lastIndexOf("."));
			String real = System.currentTimeMillis()+ext;
			System.out.println(org);
			System.out.println(real);
			// 파일저장
			String path = "D:/upload/"+real;
			try {
				file.transferTo(new File(path));
			} catch (Exception e) {}
			replyEntity.setFilename_org(org);
			replyEntity.setFilename_real(real);
		}
		
		
		ReplyEntity entity = replyRepo.save(replyEntity);

		Map<String, Object> result = new HashMap<>();
		result.put("entity", entity);
		result.put("result", entity == null ? "fail" : "success");
		return result;
	}
	
	@Transactional
	@PostMapping("/reply")
	public Map<String, Object> reply(@RequestParam Map map, @RequestParam(name = "file", required = false) MultipartFile file) {
		UserEntity userEntity = new UserEntity();
		userEntity.setNo(Integer.parseInt((String)map.get("user_no")));
		
		ReplyEntity replyEntity = new ReplyEntity();
		replyEntity.setTitle((String)map.get("title"));
		replyEntity.setContent((String)map.get("content"));
		
		// 첨부파일
		if (file != null && !file.isEmpty()) {
			// 파일명
			String org = file.getOriginalFilename();
			String ext = org.substring(org.lastIndexOf("."));
			String real = System.currentTimeMillis()+ext;
			System.out.println(org);
			System.out.println(real);
			// 파일저장
			String path = "D:/upload/"+real;
			try {
				file.transferTo(new File(path));
			} catch (Exception e) {}
			replyEntity.setFilename_org(org);
			replyEntity.setFilename_real(real);
		}
		
		int gno = Integer.parseInt((String)map.get("gno"));
		int ono = Integer.parseInt((String)map.get("ono"));
		replyEntity.setUser(userEntity);
		replyEntity.setGno(gno);
		replyEntity.setOno(ono+1);
		replyEntity.setNested(Integer.parseInt((String)map.get("nested"))+1);
		replyRepo.updateOno(gno, ono); // 같은 gno중 ono보다 큰 글 +1 처리
		ReplyEntity entity = replyRepo.save(replyEntity);
		Map<String, Object> result = new HashMap<>();
		result.put("entity", entity);
		result.put("result", entity == null ? "fail" : "success");
		return result;
	}
}
