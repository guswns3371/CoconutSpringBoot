package com.coconut.base.domain;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
  // @CreatedDate : 엔티티가 생성되어 저장될 떄의 시간이 자동 저장된다.
  @CreatedDate
  private LocalDateTime createdDate;

  // @LastModifiedDate : 조회한 엔티티의 값을 변경할 떄의 시간이 자동 저장된다.
  @LastModifiedDate
  private LocalDateTime modifiedDate;

  @PreUpdate
  public void onModifiedDateUpdate() {
    modifiedDate = LocalDateTime.now();
  }
}
