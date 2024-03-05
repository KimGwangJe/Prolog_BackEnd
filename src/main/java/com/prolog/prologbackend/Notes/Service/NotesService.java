package com.prolog.prologbackend.Notes.Service;

import com.prolog.prologbackend.Notes.DTO.Request.RequestNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface NotesService {
    public ResponseNotesListDTO getNotesList(Long memberId);
    public ResponseNotesDTO getNotes(Long notesId);
    public Long createNotes(RequestNotesDTO requestNotesDTO);
    public void updateNotes(RequestNotesDTO requestNotesDTO);
    public void deleteNotes(Long notesId,Long teamMemberId);
    public URL saveImage(MultipartFile file) throws IOException;
}
