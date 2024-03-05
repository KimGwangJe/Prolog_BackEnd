package com.prolog.prologbackend.Notes.Repository;

import com.prolog.prologbackend.Notes.Domain.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes,Long> {
    List<Notes> findAllByTeamMemberId(Long memberId);
}
