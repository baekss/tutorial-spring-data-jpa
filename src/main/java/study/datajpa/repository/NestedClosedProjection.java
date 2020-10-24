package study.datajpa.repository;

public interface NestedClosedProjection {

	public abstract String getUsername(); //member의 username 으로만 select 실행. getter메소드명 target과 일치
	public abstract TeamInfo getTeam(); //team의 모든 컬럼으로 select 실행 후 name만 추출. getter메소드명 target과 일치
	
	interface TeamInfo {
		public abstract String getName(); //getter메소드명 target과 일치
	}
}
