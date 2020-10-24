package study.datajpa.repository;

public interface MemberProjection {

	public abstract Long getId();
	public abstract String getUsername();
	public abstract String getTeamName();
}
