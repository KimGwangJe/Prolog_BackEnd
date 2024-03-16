package com.prolog.prologbackend.Notes.Repository;

import com.prolog.prologbackend.Notes.Domain.Notes;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes,Long> {
    List<Notes> findAllByTeamMemberId(Long memberId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notes n WHERE n.teamMember.id IN :teamMemberIds")
    void deleteNotesByTeamMemberIds(List<Long> teamMemberIds);
}
