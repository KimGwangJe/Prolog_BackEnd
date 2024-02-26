package com.prolog.prologbackend.Notes.Service;

import com.prolog.prologbackend.Notes.DTO.NotesListResponseDTO;
import org.springframework.stereotype.Service;

/**
 * Author : Kim
 * Date : 2024-02-16
 * Description :
*/
@Service
public class NotesServiceImpl implements NotesService {
    @Override
    public NotesListResponseDTO getProjectNotes(Long memberId, Long projectId) {
        NotesListResponseDTO notesListResponseDTO = new NotesListResponseDTO();
        return notesListResponseDTO;
    }
}
