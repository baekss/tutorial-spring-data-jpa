package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {
	
	@Autowired 
	MemberJpaRepository memberJpaRepository;
	
	@Test
	public void testMemberWithMemberRepository() {
		Member member = new Member("MemberA");
		Member savedMember = memberJpaRepository.save(member);
		
		Member findMember = memberJpaRepository.findById(savedMember.getId()).get();
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}
	
	@Test
	public void basicCRUDWithMemberJpaRepository() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		
		Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
		Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
		
		//단건 조회 검증
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		
		//리스트 조회 검증
		List<Member> members = memberJpaRepository.findAll();
		assertThat(members.size()).isEqualTo(2);
		
		//카운트 검증
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(2);
		
		//삭제 검증
		memberJpaRepository.delete(member1);
		memberJpaRepository.delete(member2);
		
		//카운트 검증
		long deletedCount = memberJpaRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}
	
	@Test
	public void testFindByUsernameAndAgeGreaterThan() {
		Member m1 = new Member("감녕", 10, null);
		Member m2 = new Member("감녕", 20, null);
		memberJpaRepository.save(m1);
		memberJpaRepository.save(m2);
		
		List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("감녕", 15);
		assertThat(members.size()).isEqualTo(1);
		assertThat(members.get(0).getUsername()).isEqualTo("감녕");
		assertThat(members.get(0).getAge()).isEqualTo(20);
	}
	
	@Test
	public void testNamedQuery() {
		Member m1 = new Member("감녕", 10, null);
		Member m2 = new Member("능통", 20, null);
		Member m3 = new Member("능통", 20, null);
		memberJpaRepository.save(m1);
		memberJpaRepository.save(m2);
		memberJpaRepository.save(m3);
		
		List<Member> members = memberJpaRepository.findByUsername("능통");
		assertThat(members.get(0)).isEqualTo(m2); //true
		assertThat(members.get(1)).isEqualTo(m3); //true
	}
}
