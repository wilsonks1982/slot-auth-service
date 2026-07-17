package org.wilsonks.slotauthservice.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wilsonks.slotauthservice.domain.Player;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByUid(String uid);
    Optional<Player> findByUid(String uid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Player p WHERE p.uid = :uid")
    Optional<Player> findByUidForUpdate(String uid);
}
