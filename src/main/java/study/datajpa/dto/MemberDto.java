package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data
public class MemberDto {

	private Long id;
	private String username;
	private String teamName;
	
	public MemberDto(Member member) {
		this.id = member.getId();
		this.username = member.getUsername();
		this.teamName = "재야";
	}

	public MemberDto(Long id, String username, String teamName) {
		super();
		this.id = id;
		this.username = username;
		this.teamName = teamName;
	}
}
