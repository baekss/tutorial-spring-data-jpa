package study.datajpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	/** namedQuery를 먼저 찾고 없으면 메소드명에 의한 쿼리 자동 생성 사용 */
	
	public abstract List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
	
	public abstract List<Member> findMemberBy(); //전체조회
	
	@Query(name="Member.findByUsername")
	public abstract List<Member> findByUsername(@Param("username") String username);
}
