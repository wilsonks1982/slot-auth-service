package org.wilsonks.slotauthservice.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "players")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "uid", nullable = false, unique = true, length = 8)
    private String uid;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "pin", nullable = false)
    private String pin;

    @Column(name = "onHold")
    private Boolean onHold = false;

    @Column(name = "isPlaying")
    private Boolean isPlaying = false;

    @Column(name = "createdAt", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updatedAt", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(uid, player.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid);
    }
}
