package org.wilsonks.slotauthservice.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, unique = true, length = 8)
    private String uid;

    @Column(name = "account", nullable = false, unique = true, length = 20)
    private String account;

    @Column(name = "pin", nullable = false)
    private String pin;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private EmployeeRole role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "lastReset", nullable = true)
    private Instant lastReset;

    @Column(name = "created_at", nullable = false , updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        lastReset = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        //Hibernate uses proxy classes, so we use getClass() instead of instanceof to ensure proper equality checks
        if(o == null || this.getClass() != o.getClass()) return false;
        Employee other = (Employee) o;
        return Objects.equals(this.uid, other.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid);
    }

}
