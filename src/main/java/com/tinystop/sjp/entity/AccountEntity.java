package com.tinystop.sjp.entity;

import java.util.List;
import com.tinystop.sjp.entity.OrderEntity;
import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Builder
@Getter
@Entity
@Table(name="ACCOUNT_TABLE")
public class AccountEntity extends BaseEntity {

    @Id // primary key
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long userID;

    @Column(name = "NAME", nullable = false, length = 15) // not null, varchar(15)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 20)
    private String password;

    @Column(name = "EAMIL", length = 30)
    private String email;

    @OneToMany
    private List<OrderEntity> orders;

}
