package study.datajpa.repository;

public class UsernameImplDto {

	private String memberName;
	private int memberAge;
	
	//생성자의 매개변수명이 Entity필드명과 일치해야함
	//member의 username, age 로만 select 실행
	public UsernameImplDto(String username, int age) {
		this.memberName = username;
		this.memberAge = age;
	}

	public String getMemberName() {
		return memberName;
	}

	public int getMemberAge() {
		return memberAge;
	}
	
}
