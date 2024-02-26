package com.prolog.prologbackend.Notes.Service;

import com.prolog.prologbackend.Notes.DTO.NotesListResponseDTO;

public interface NotesService {
    public NotesListResponseDTO getProjectNotes(Long memberId, Long projectId);
}
