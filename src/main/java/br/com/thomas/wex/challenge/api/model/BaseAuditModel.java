package br.com.thomas.wex.challenge.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Data
@ToString
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditModel implements Serializable {

    private static final long serialVersionUID = -8473508689974561028L;

    @Version
    @Column(name = "version")
    @ToString.Exclude
    protected Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @ToString.Exclude
    protected LocalDateTime createdAt;

//    @CreatedBy
//    @ToString.Exclude
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by", updatable = false)
//    protected Usuario createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @ToString.Exclude
    protected LocalDateTime updatedAt;

//    @LastModifiedBy
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "updated_by")
//    @ToString.Exclude
//    protected Usuario updatedBy;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    protected boolean deleted = false;
}
