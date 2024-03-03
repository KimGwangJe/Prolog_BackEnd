package com.prolog.prologbackend.Notes.Service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Domain.MemberStatus;
import com.prolog.prologbackend.Notes.DTO.NotesType;
import com.prolog.prologbackend.Notes.DTO.Request.RequestNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesListDTO;
import com.prolog.prologbackend.Notes.Domain.Notes;
import com.prolog.prologbackend.Notes.ExceptionType.NotesExceptionType;
import com.prolog.prologbackend.Notes.Repository.ImageRepository;
import com.prolog.prologbackend.Notes.Repository.NotesRepository;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Exception.TeamMemberExceptionType;
import com.prolog.prologbackend.TeamMember.Repository.TeamMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class NotesServiceImplTest {

    @Mock
    private NotesRepository notesRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private NotesServiceImpl notesService;

    @Value("${S3Bucket}")
    private String bucket;

    @Test
    @DisplayName("Get 일지 리스트")
    void 일지_리스트_GET() {
        Long memberId = 1L;
        Notes notes = makeNotes();

        List<Notes> mockNotesList = new ArrayList<>();
        mockNotesList.add(notes);
        Mockito.when(notesRepository.findAllByTeamMemberId(anyLong())).thenReturn(mockNotesList);

        List<Notes> notesList = new ArrayList<>(notesRepository.findAllByTeamMemberId(memberId));
        ResponseNotesListDTO responseNotesListDTO = new ResponseNotesListDTO();
        List<ResponseNotesDTO> list = new ArrayList<>();
        for(int i = 0; i < notesList.size(); i++){
            ResponseNotesDTO responseNotesDTO = makeResponseNotesDTO(notesList.get(i));
            list.add(responseNotesDTO);
        }
        responseNotesListDTO.setNotesList(list);

        assertNotNull(responseNotesListDTO); // 응답이 null이 아닌지 확인
        assertEquals(notes.getNotesId(), responseNotesListDTO.getNotesList().get(0).getNotesId());
        assertEquals(NotesType.valueOf(notes.getType()), NotesType.valueOf(String.valueOf(responseNotesListDTO.getNotesList().get(0).getType())));
        assertEquals(notes.getContent(), responseNotesListDTO.getNotesList().get(0).getContent());
    }

    @Test
    @DisplayName("Get프로젝트")
    void 일지_Get() {
        Long notesId = 1L;
        Notes mockNotes = makeNotes();

        Mockito.when(notesRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockNotes));
        Notes notes = notesRepository.findById(notesId).orElseThrow(()-> new BusinessLogicException(NotesExceptionType.NOTES_NOT_FOUND));

        ResponseNotesDTO responseNotesDTO = makeResponseNotesDTO(notes);

        assertNotNull(responseNotesDTO); // 응답이 null이 아닌지 확인
        assertEquals(notes.getNotesId(), responseNotesDTO.getNotesId());
        assertEquals(NotesType.valueOf(notes.getType()), NotesType.valueOf(String.valueOf(responseNotesDTO.getType())));
        assertEquals(notes.getContent(), responseNotesDTO.getContent());
    }

    @Test
    @DisplayName("일지 생성")
    void 일지_생성() {
        Notes mockNotes = makeNotes();

        Mockito.when(notesRepository.save(any(Notes.class))).thenReturn(mockNotes);
        Notes notes = notesRepository.save(mockNotes);

        assertNotNull(notes);

        assertEquals(mockNotes.getNotesId(), notes.getNotesId());
        assertEquals(NotesType.valueOf(mockNotes.getType()), NotesType.valueOf(String.valueOf(notes.getType())));
        assertEquals(mockNotes.getContent(), notes.getContent());
    }

    @Test
    @DisplayName("일지 수정")
    void 일지_수정() {
        Notes mockNotes = makeNotes();

        Mockito.when(notesRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockNotes));

        RequestNotesDTO requestNotesDTO = new RequestNotesDTO();
        requestNotesDTO.setNotesId(1L);
        requestNotesDTO.setSummary("aaaaa");
        requestNotesDTO.setContent("a");
        requestNotesDTO.setType(NotesType.valueOf("Blog"));
        requestNotesDTO.setDate(new Date());
        requestNotesDTO.setTitle("a");
        requestNotesDTO.setMemberId(1L);

        notesService.updateNotes(requestNotesDTO);
        assertNotEquals(mockNotes.getSummary(), requestNotesDTO.getSummary());
    }

    @Test
    @DisplayName("일지 삭제")
    void 일지_삭제() {
        Long notesId = 1L;
        Long memberId = 1L;
        Notes mockNotes = makeNotes();

        lenient().when(notesRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockNotes));
        lenient().doNothing().when(notesRepository).delete(any(Notes.class));

        assertDoesNotThrow(() -> notesService.deleteNotes(notesId,memberId));
    }

    public Notes makeNotes(){
        Long memberId = 1L;
        Member member = Member.builder()
                .nickname("test")
                .phone("01011112222")
                .email("test1234@naver.com")
                .roles("Leader")
                .profileImage("test")
                .profileName("test")
                .status(MemberStatus.valueOf("VERIFIED"))
                .password("test")
                .isDeleted(false)
                .build();
        Project project = Project.builder()
                .projectId(1L)
                .stack("1")
                .description("test")
                .endedDate(new Date())
                .modifiedDate(new Date())
                .startDate(new Date())
                .projectName("test")
                .createdDate(new Date())
                .isDeleted(false)
                .build();

        // 이 부분에서 member와 project를 사용하여 TeamMember 객체를 생성하도록 수정합니다.
        TeamMember teamMember = TeamMember.builder()
                .part("Leader")
                .member(member)
                .project(project)
                .build();

        Mockito.when(teamMemberRepository.findById(anyLong())).thenReturn(Optional.of(teamMember));

        TeamMember mockTeamMember = teamMemberRepository.findById(memberId).orElseThrow(
                () -> new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND));

        Notes notes = Notes.builder()
                .notesId(1L)
                .date(new Date())
                .type("Blog")
                .teamMember(mockTeamMember)
                .title("a")
                .content("a")
                .createdDate(new Date())
                .modifiedDate(new Date())
                .summary("a")
                .build();

        return notes;
    }

    public ResponseNotesDTO makeResponseNotesDTO(Notes notes){
        ResponseNotesDTO responseNotesDTO = ResponseNotesDTO.builder()
                .notesId(notes.getNotesId())
                .title(notes.getTitle())
                .type(NotesType.valueOf(notes.getType()))
                .summary(notes.getSummary())
                .date(notes.getDate())
                .createdDate(notes.getCreatedDate())
                .modifiedDate(notes.getModifiedDate())
                .content(notes.getContent())
                .build();
        return responseNotesDTO;
    }
}