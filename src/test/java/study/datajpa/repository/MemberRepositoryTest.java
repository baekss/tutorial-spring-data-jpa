package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

	@Autowired 
	MemberRepository memberRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Test
	public void testMemberWithMemberRepository() {
		Member member = new Member("MemberA");
		Member savedMember = memberRepository.save(member);
		
		Member findMember = memberRepository.findById(savedMember.getId()).get();
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}
	
	@Test
	public void basicCRUDWithMemberJpaRepository() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		
		//단건 조회 검증
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		
		//리스트 조회 검증
		List<Member> members = memberRepository.findAll();
		assertThat(members.size()).isEqualTo(2);
		
		//카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);
		
		//삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);
		
		//카운트 검증
		long deletedCount = memberRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}
	
	@BeforeEach
	private void saveTeamAndMember() {
		Team team = new Team("오나라");
		teamRepository.save(team);
		
		Member m1 = new Member("감녕", 10, team);
		Member m2 = new Member("능통", 20, team);
		memberRepository.save(m1);
		memberRepository.save(m2);
	}
	
	@Test
	public void testFindByUsernameAndAgeGreaterThan() {
		List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("능통", 15);
		assertThat(members.size()).isEqualTo(1);
		assertThat(members.get(0).getUsername()).isEqualTo("능통");
		assertThat(members.get(0).getAge()).isEqualTo(20);
	}
	
	@Test
	public void testFindMemberBy() {
		List<Member> members = memberRepository.findMemberBy();
		assertThat(members.size()).isEqualTo(2);
	}
	
	@Test
	public void testNamedQuery() {
		Member m1 = new Member("감녕", 10, null);
		Member m2 = new Member("능통", 20, null);
		Member m3 = new Member("능통", 20, null);
		memberRepository.save(m1);
		memberRepository.save(m2);
		memberRepository.save(m3);
		
		List<Member> members = memberRepository.findByUsername("능통");
		assertThat(members.get(0)).isEqualTo(m2); //true
		assertThat(members.get(1)).isEqualTo(m3); //true
	}
	
	@Test
	public void testQuery() {
		List<Member> members = memberRepository.findUser("능통", 20);
		members.stream().forEach(m->System.out.println(m.getTeam().getName()));
	}
	
	@Test
	public void testQueryResultForDto() {
		List<String> usernames = memberRepository.findUsernames();
		usernames.stream().forEach(System.out::println);
		
		List<MemberDto> dtos = memberRepository.findMemberDto();
		dtos.stream().forEach(System.out::println);
	}
	
	@Test
	public void testFindByNames() {
		List<Member> members = memberRepository.findByNames(Arrays.asList("감녕","능통"));
		members.stream().forEach(System.out::println);
	}
	
	@Test
	public void testReturnType() {
		//조건에 해당하는 것이 없으면 emptyList가 반환된다.
		List<Member> members = memberRepository.findListByUsername("능통");
		members.stream().forEach(System.out::println);
		
		//조건에 해당하는 것이 없으면 null이 반환된다(jpa와 data-jpa의 차이점).
		Member member = memberRepository.findMemberByUsername("능통");
		System.out.println(member);
		
		//조건에 해당하는 것이 없으면 Optional.empty가 반환된다.
		Optional<Member> optional = memberRepository.findOptionalByUsername("능통");
		System.out.println(optional.get());
		
		//Single Result를 받아야 하는데 다 건 조회가 된다면 IncorrectResultSizeDataAccessException(Caused by: javax.persistence.NonUniqueResultException)
		
		Member m3 = new Member("능통", 20, null);
		memberRepository.save(m3);
		
		Optional<Member> o = memberRepository.findOptionalByUsername("능통");
		System.out.println(o.get());
	}
}
