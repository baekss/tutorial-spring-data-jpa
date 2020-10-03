package study.datajpa.entity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberTest {

	@PersistenceContext
	EntityManager em;
	
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
}
