package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	/** namedQuery를 먼저 찾고 없으면 메소드명에 의한 쿼리 자동 생성 사용 */
	
	public abstract List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
	
	public abstract List<Member> findMemberBy(); //전체조회
	
	@Query(name="Member.findByUsername")
	public abstract List<Member> findByUsername(@Param("username") String username);
	
	@Query("select m from Member m where m.username = :username and m.age = :age")
	public abstract List<Member> findUser(@Param("username") String username, @Param("age") int age);
	
	@Query("select m.username from Member m")
	public abstract List<String> findUsernames();
	
	@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	public abstract List<MemberDto> findMemberDto();
	
	@Query("select m from Member m where m.username in :names")
	public abstract List<Member> findByNames(@Param("names") Collection<String> names);
	
	List<Member> findListByUsername(String username); //컬렉션
	Member findMemberByUsername(String username); //단건
	Optional<Member> findOptionalByUsername(String username); //단건 Optional
	
	Page<Member> findByAge(int age, Pageable pageable);
	
	Slice<Member> findMemberByAge(int age, Pageable pageable);
	
	//카운트 쿼리가 조회 쿼리를 기반으로 실행되기 때문에, 조회 쿼리가 성능이 안 좋은 쿼리라면
	//동일한 결과를 얻을 수 있는 카운트 쿼리를 따로 작성하자 
	@Query(value="select m from Member m left join m.team t", countQuery="select count(m) from Member m")
	Page<Member> findAll(int age, Pageable pageable);
	
	@Modifying(clearAutomatically = true)
	@Query("update Member m set m.age = m.age + 2 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);
}
