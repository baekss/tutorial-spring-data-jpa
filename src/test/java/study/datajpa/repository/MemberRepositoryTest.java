package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
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
	
	@PersistenceContext
	private EntityManager em;
	
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
	
	@Test
	public void testPaging() {
		Member m3 = new Member("주태", 10, null);
		Member m4 = new Member("육손", 10, null);
		memberRepository.save(m3);
		memberRepository.save(m4);
		
		int age = 10;
		
		/*
		 * PageRequest.of(해당 페이지, 한 페이지에 넣을 아이템 개수, 정렬 조건)
		 * 1. 조회 된 아이템들을 정렬 조건에 의해 정렬 후
		 * 2. 두번째 인자값으로 설정된 값만큼 앞에서부터 그룹화
		 * 3. 앞에 그룹 부터 0...N 페이지로 구분
		 * 한 페이지에 두개씩이면, 두번째 페이지 일때 offset : 1*2, limit : 2, 세번째 페이지 일때 offset : 2*2, limit : 2
		 */
		PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "username"));
		
		//마지막 페이지 조회 조건으로 호출했을 때는 카운트 쿼리가 실행되지 않는다(ex. (이전 페이지 수 * 아이템 수) + 현재 페이지 아이템 수로 계산 가능). 
		Page<Member> page = memberRepository.findByAge(age, pageRequest);
		
		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();
		
		for(Member member : content) {
			System.out.println(member);
		}
		System.out.println("totalElements "+totalElements);
		
		assertThat(content.size()).isEqualTo(2); //0페이지에는 "주태, 육손"
		assertThat(totalElements).isEqualTo(3); //나이(age)가 10살은 총 3명
		assertThat(page.getNumber()).isEqualTo(0); //현재 0페이지 보기 상태임
		assertThat(page.getTotalPages()).isEqualTo(2); //4명을 2명씩 그룹화해서 2페이지까지임
		assertThat(page.isFirst()).isTrue(); //첫번째 페이지를 보고 있음
		assertThat(page.isLast()).isFalse(); //마지막 페이지를 보고 있지 않음
		assertThat(page.hasNext()).isTrue(); //다음 페이지 있음
		
		//Slice는 다음 페이지가 존재하는지 확인할 수 있도록 쿼리를 실행함(ex. limit : limit + 1) 
		Slice<Member> slice = memberRepository.findMemberByAge(age, pageRequest);
		List<Member> sliceContent = slice.getContent();
		assertThat(sliceContent.size()).isEqualTo(2);
		assertThat(slice.getNumber()).isEqualTo(0);
		assertThat(slice.isFirst()).isTrue();
		assertThat(slice.isLast()).isFalse();
		assertThat(slice.hasNext()).isTrue();
	}
	
	@Test
	public void testPage2() {
		Member m3 = new Member("주태", 10, null);
		Member m4 = new Member("육손", 10, null);
		memberRepository.save(m3);
		memberRepository.save(m4);
		
		PageRequest pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "username"));
		
		Page<Member> page = memberRepository.findAll(10, pageRequest);
		
		//Entity를 Dto로 변환한 Page를 사용
		Page<MemberDto> pageDto = page.map(m->new MemberDto(m.getId(), m.getUsername(), null));
		
		assertThat(pageDto.getContent().size()).isEqualTo(2);
		assertThat(pageDto.getTotalElements()).isEqualTo(4);
	}
	
	@Test
	@Rollback(false)
	public void testBulkAgePlus() {
		Member m3 = new Member("주태", 25, null);
		Member m4 = new Member("육손", 30, null);
		memberRepository.save(m3);
		memberRepository.save(m4);
		
		int resultRows = memberRepository.bulkAgePlus(20);
		//영속성 컨텍스트를 비워, 1차 캐시에서 데이터를 조회하지 않도록 함
		//@Modifying(clearAutomatically = true) 사용 또는 em.clear(); 
		
		List<Member> members = memberRepository.findByUsername("육손");
		Member member = members.get(0);
		System.out.println(member);
		
		assertThat(resultRows).isEqualTo(3);
	}
	
	@Test
	public void findMemberLazy() {
		//given
		//member1 -> teamA
		//member2 -> teamB
		
		Team teamB = new Team("위나라");
		teamRepository.save(teamB);
		Member m3 = new Member("하후돈", 25, teamB);
		Member m4 = new Member("하후연", 30, teamB);
		memberRepository.save(m3);
		memberRepository.save(m4);
		
		em.flush();
		em.clear();
		
		List<Member> members = memberRepository.findAll();
		
		//findAll 오버라이딩 이전에는 프록시 객체를 주입받는다.(N+1쿼리)
		members.stream().forEach(m -> {
									System.out.println(m.getClass()); //study.datajpa.entity.Member
									System.out.println(m);
									System.out.println(m.getTeam().getClass()); //study.datajpa.entity.Team$HibernateProxy$EMxcw1ew
									System.out.println(m.getTeam().getName());
								});
	}
	
	@Test
	public void findMemberFetchJoin() {
		Team teamB = new Team("위나라");
		teamRepository.save(teamB);
		Member m3 = new Member("하후돈", 25, teamB);
		Member m4 = new Member("하후연", 30, teamB);
		memberRepository.save(m3);
		memberRepository.save(m4);
		
		List<Member> members = memberRepository.findMemberFetchJoin();
		members.stream().forEach(m -> {
									System.out.println(m.getClass()); //study.datajpa.entity.Member
									System.out.println(m);
									System.out.println(m.getTeam().getClass()); //study.datajpa.entity.Team
									System.out.println(m.getTeam().getName());
								});
	}
	
	@Test
	public void testQueryHint() {
		Member m3 = new Member("하후돈", 25, null);
		memberRepository.save(m3);
		em.flush();
		em.clear();
		
		Member findMember = memberRepository.findReadOnlyByUsername(m3.getUsername());
		//@QueryHints를 readOnly로 설정했기 때문에 변경감지에 의한 update 쿼리 실행 안됨. 
		findMember.setUsername("하후은");
		
		em.flush();
	}
	
	@Test
	public void testLock() {
		//Lock설정에 의해 select .. for update 쿼리가 실행
		List<Member> members = memberRepository.findLockByUsername("감녕");
	}
	
	@Test
	public void testCallCustom() {
		/*
		 * MemberRepository가 extends 한 MemberRepositoryCustom 인터페이스의
		 * 구현체 MemberRepositoryImpl의 findMemberCustom 메소드를 호출함.
		 */
		memberRepository.findMemberCustom();
	}
	
	@Test
	public void specBasic() {
		em.flush();
		em.clear();
		
		Specification<Member> spec = MemberSpec.username("감녕").and(MemberSpec.teamName("오나라"));
		List<Member> result = memberRepository.findAll(spec);
		
		assertThat(result.size()).isEqualTo(1);
	}
	
	@Test
	public void queryExample() {
		em.flush();
		em.clear();
		/**
			select
		        member0_.member_id as member_i1_1_,
		        member0_.created_date as created_2_1_,
		        member0_.last_modified_date as last_mod3_1_,
		        member0_.created_by as created_4_1_,
		        member0_.last_modified_by as last_mod5_1_,
		        member0_.age as age6_1_,
		        member0_.team_id as team_id8_1_,
		        member0_.username as username7_1_ 
		    from
		        member member0_ 
		    inner join
		        team team1_ 
		            on member0_.team_id=team1_.team_id 
		    where
		        team1_.name=? 
		        and member0_.username=?
		*/
		Member member = new Member("감녕");
		member.changeTeam(new Team("오나라"));
		
		//outer join 에는 사용불가
		//기본정책으로 null은 무시, 다만 primitive 타입은 초기화값 사용(primitive 타입 무시 필요시 ExampleMatcher 이용)
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
		
		Example<Member> example = Example.of(member, matcher);
		List<Member> result = memberRepository.findAll(example);
		
		assertThat(result.get(0).getUsername()).isEqualTo("감녕");
	}
	
	@Test 
	public void testProjections() {
		//UsernameDto를 jdk dynamic proxy를 통해 구현체를 생성
		List<UsernameDto> usernames = memberRepository.findProjectionsByUsername("능통");
		usernames.stream().forEach(u->System.out.println(u.getUsername()));
		
		//구현체로 조회
		List<UsernameImplDto> users = memberRepository.findProjectionsBy();
		users.stream().forEach(u->System.out.println(u.getMemberName()+" "+u.getMemberAge()));
		
		//제네릭타입으로 동적인 조회1
		List<UsernameImplDto> result1 = memberRepository.<UsernameImplDto>findProjectionsByUsername("능통", UsernameImplDto.class);
		result1.stream().forEach(r->System.out.println(r.getMemberName()));
		
		//제네릭타입으로 동적인 조회2
		List<UsernameDto> result2 = memberRepository.<UsernameDto>findProjectionsByUsername("능통", UsernameDto.class);
		result2.stream().forEach(r->System.out.println(r.getUsername()));
		
		/**
		select
	        member0_.username as col_0_0_,
	        team1_.team_id as col_1_0_,
	        team1_.team_id as team_id1_2_,
	        team1_.created_date as created_2_2_,
	        team1_.last_modified_date as last_mod3_2_,
	        team1_.name as name4_2_ 
	    from
	        member member0_ 
	    left outer join
	        team team1_ 
	            on member0_.team_id=team1_.team_id 
	    where
	        member0_.username=?
		 */
		List<NestedClosedProjection> nestedResult = memberRepository.<NestedClosedProjection>findProjectionsByUsername("능통", NestedClosedProjection.class);
		nestedResult.stream().forEach(nr->System.out.println(nr.getUsername()+" "+nr.getTeam().getName()));
	}
	
	@Test
	public void nativeQuery() {
		Member result = memberRepository.findByNativeQuery("능통");
		System.out.println("result: "+ result);
		
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "username"));
		Page<MemberProjection> page = memberRepository.findByNativeProjection(pageRequest);
		List<MemberProjection> memberPros = page.getContent();
		memberPros.forEach(m -> System.out.println(m.getId() + " " + m.getUsername() + " " + m.getTeamName()));
	}
}
