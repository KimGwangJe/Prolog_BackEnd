package com.prolog.prologbackend.Member.Repository;

import com.prolog.prologbackend.Member.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    @Modifying
    @Query("update member m set m.isDeleted = true, m.modifiedDate = ?1 where m.id = ?2")
    void updateMemberStatus(LocalDateTime updateDate, Long memberId);
}
