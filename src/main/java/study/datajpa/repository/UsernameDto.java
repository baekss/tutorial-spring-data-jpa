package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameDto {
	@Value("#{target.username + ' ' + target.age}") //open projection : member의 모든 컬럼으로 select 실행 후 username, age만 추출
	public abstract String getUsername(); //close projection : member의 username 으로만 select 실행. getter메소드명 target과 일치
}
