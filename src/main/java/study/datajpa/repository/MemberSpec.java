package study.datajpa.repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

public class MemberSpec {

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
	
	public static Specification<Member> teamName(final String teamName) {
		return (Specification<Member>) (root, query, builder) -> {
			if(StringUtils.isEmpty(teamName)) {
				return null;
			}
			
			Join<Member, Team> t = root.join("team", JoinType.INNER);
			return builder.equal(t.get("name"), teamName);
		};
	}
	
	public static Specification<Member> username(final String username) {
		return (Specification<Member>) (root, query, builder) -> {
			return builder.equal(root.get("username"), username);
		};
	}
}
