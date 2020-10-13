package study.datajpa.repository;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

@RequiredArgsConstructor
//기 존재하는 Repository명(ex. MemberRepository) + Impl 명명규칙을 반드시 지킨다.
public class MemberRepositoryImpl implements MemberRepositoryCustom{

	private final EntityManager em;
	
	@Override
	public List<Member> findMemberCustom() {
		//MyBatis 또는 QueryDSL 등을 사용하여 별도의 작업이 존재할 땐 ***RepositoryCustom과 같은 역할의 인터페이스를 활용하는 것이 효율적이다.
		return em.createQuery("select m from Member m",Member.class)
				.getResultList();
	}

}
