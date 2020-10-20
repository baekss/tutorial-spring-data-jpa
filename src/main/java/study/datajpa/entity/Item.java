package study.datajpa.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Item implements Persistable<String>{

	//해당 객체는 식별키 자동생성 전략이 아니다.
	//따라서 data-jpa의 save 시 persist가 아닌 merge로 빠진다.
	//merge는 식별키로 select를 해보고 없으면 insert를 하기 때문에 비효율적임.
	//Persistable 인터페이스의 isNew 메소드를 오버라이딩하고 @CreatedDate의 메커니즘을 이용해 new 여부를 구현한다.
	/**
	@Transactional
	@Override
	public <S extends T> S save(S entity) {

		if (entityInformation.isNew(entity)) {
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}
	*/
	
	@Id
	private String id;

	@CreatedDate
	private LocalDateTime createdDate;
	
	public Item(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return createdDate == null;
	}
	
}
