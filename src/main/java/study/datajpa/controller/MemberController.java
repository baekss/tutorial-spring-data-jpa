package study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberRepository memberRepository;
	
	@GetMapping("/members/{id}")
	public String findMember(@PathVariable("id") Long id) {
		Member member = memberRepository.findById(id).orElse(new Member("재야장수"));
		return member.getUsername();
	}
	
	//조회용으로 사용 가능하다. memberRepository.findById 메소드 내부적으로 사용함
	@GetMapping("/members2/{id}")
	public String findMember(@PathVariable("id") Member member) {
		return member.getUsername();
	}
	
	//http://localhost:8080/members?page=3&size=5&sort=id,desc
	@GetMapping("/members")
	//http://localhost:8080/members 로 요청시 디폴트(@PageableDefault(로컬) 또는 yml(글로벌)로 설정)
	public Page<MemberDto> list(@PageableDefault(size=5, sort="username", direction=Direction.ASC) Pageable pageable) {
		Page<Member> page = memberRepository.findAll(pageable);
		Page<MemberDto> map = page.map(MemberDto::new);
		return map;
	}
	
	@PostConstruct
	public void init() {
		for(int i=0 ; i<100 ; i++) {
			Member m1 = new Member("감녕"+i, i, null);
			memberRepository.save(m1);
		}
	}
}
