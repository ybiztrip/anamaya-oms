package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.PaxTitle;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(name = "company_id")
    private Long companyId;

    @Column(nullable = false, length = 256)
    private String email;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(name = "first_name", length = 256)
    private String firstName;

    @Column(name = "last_name", length = 256)
    private String lastName;

    @Column(length = 20)
    private String gender;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "phone_no", length = 256)
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PaxTitle title;

    @Column(name = "identity_no", length = 100)
    private String identityNo;

    @Column(name = "passport_no", length = 100)
    private String passportNo;

    @Column(name = "passport_expiry")
    private Date passportExpiry;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(nullable = false)
    private Short status;

    private Boolean enableChatEngine;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserRole> userRoles;
}

