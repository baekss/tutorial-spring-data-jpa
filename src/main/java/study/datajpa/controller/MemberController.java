package study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
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
	
	@PostConstruct
	public void init() {
		Member m1 = new Member("감녕", 10, null);
		memberRepository.save(m1);
	}
}
