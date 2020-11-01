package study.datajpa.entity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

@SpringBootTest
@Transactional
//@Rollback(false)
@Commit
public class MemberTest {

	@PersistenceContext
	EntityManager em;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Test
	public void testEntity() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		em.persist(teamA);
		em.persist(teamB);
		
		Member member1 = new Member("Member1", 10, teamA);
		Member member2 = new Member("Member2", 15, teamA);
		Member member3 = new Member("Member3", 20, teamB);
		Member member4 = new Member("Member4", 25, teamB);
		
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		
		//초기화
		em.flush();
		em.clear();
		
		//확인
		List<Member> members = em.createQuery("select m from Member m", Member.class)
							.getResultList();
		
		members.stream().forEach(m->{System.out.println(m.toString()+" "+m.getTeam().toString());});
	}
	
	@Test
	public void JpaEventBaseEntity() throws Exception {
		//given
		Team team = new Team("오나라");
		teamRepository.save(team);
		
		Member member = new Member("감녕");
		member.changeTeam(team);
		memberRepository.save(member);
		
		Thread.sleep(1000);
		team.setName("위나라");
		member.setUsername("황개");
		
		em.flush();
		em.clear();
		
		//when
		Member findMember = memberRepository.findById(member.getId()).get();
		
		//then
		System.out.println("findMember " + findMember.getUsername()); //황개
		System.out.println("findMemberCreatedDate " + findMember.getCreatedDate()); //2020-10-17T14:49:34.982335
		System.out.println("findMemberUpdatedDate " + findMember.getLastModifiedDate()); //2020-10-17T14:49:35.992393
		System.out.println("findMemberCreatedBy " + findMember.getCreatedBy()); //720d11af-dc1c-45da-a7df-35f29724321f
		System.out.println("findMemberLastModifiedBy " + findMember.getLastModifiedBy()); //a13c018b-7905-4ecc-9f13-724a198d4724
		System.out.println("findTeamName " + findMember.getTeam().getName()); //위나라
		System.out.println("findTeamCreatedDate " + findMember.getTeam().getCreatedDate()); //2020-10-17T14:49:34.925332
		System.out.println("findTeamLastModifiedDate " + findMember.getTeam().getLastModifiedDate()); //2020-10-17T14:49:35.988392
	}
	
	@Test
	public void addMember() {
		Team team = new Team("오나라");
		teamRepository.save(team);
		
		Member member = new Member("손책");
		member.changeTeam(team);
		memberRepository.save(member);
		em.flush();
		em.clear();
		Member member2 = new Member("손견",20, teamRepository.findById(101L).get()); //영속화된 Team을 셋팅함
		memberRepository.save(member2);
		em.flush();
		em.clear();
	}
}
