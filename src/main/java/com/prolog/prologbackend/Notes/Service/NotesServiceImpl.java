package com.prolog.prologbackend.Notes.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Notes.DTO.NotesType;
import com.prolog.prologbackend.Notes.DTO.Request.RequestNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesListDTO;
import com.prolog.prologbackend.Notes.Domain.Image;
import com.prolog.prologbackend.Notes.Domain.Notes;
import com.prolog.prologbackend.Notes.ExceptionType.NotesExceptionType;
import com.prolog.prologbackend.Notes.Repository.ImageRepository;
import com.prolog.prologbackend.Notes.Repository.NotesRepository;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Exception.TeamMemberExceptionType;
import com.prolog.prologbackend.TeamMember.Repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;

    private final ImageRepository imageRepository;

    private final AmazonS3Client amazonS3Client;

    private final AmazonS3 s3Client;

    private final TeamMemberRepository teamMemberRepository;

    @Value("${S3Bucket}")
    private String bucket;

    @Override
    @Transactional(readOnly = true)
    public ResponseNotesListDTO getNotesList(Long memberId) {
        List<Notes> notesList = new ArrayList<>(notesRepository.findAllByTeamMemberId(memberId));
        ResponseNotesListDTO responseNotesListDTO = new ResponseNotesListDTO();
        List<ResponseNotesDTO> list = new ArrayList<>();
        for(int i = 0; i < notesList.size(); i++){
            ResponseNotesDTO responseNotesDTO = makeResponseNotesDTO(notesList.get(i));
            list.add(responseNotesDTO);
        }
        responseNotesListDTO.setNotesList(list);
        return responseNotesListDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseNotesDTO getNotes(Long notesId) {
        Notes notes = notesRepository.findById(notesId).orElseThrow(
                () -> new BusinessLogicException(NotesExceptionType.NOTES_NOT_FOUND));
        ResponseNotesDTO responseNotesDTO = makeResponseNotesDTO(notes);
        return responseNotesDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotes(RequestNotesDTO requestNotesDTO) {
        TeamMember teamMember = teamMemberRepository.findById(requestNotesDTO.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND));

        Notes notes = makeNotesEntity(requestNotesDTO,teamMember);

        try{
            Notes createdNotes = notesRepository.save(notes);
            linkImagesToNotes(createdNotes.getContent(),createdNotes);
            return createdNotes.getNotesId();
        } catch(Exception e){
            throw new BusinessLogicException(NotesExceptionType.NOTES_SAVE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotes(RequestNotesDTO requestNotesDTO) {
        Notes notes = notesRepository.findById(requestNotesDTO.getNotesId()).orElseThrow(
                () -> new BusinessLogicException(NotesExceptionType.NOTES_NOT_FOUND));

        TeamMember teamMember = teamMemberRepository.findById(requestNotesDTO.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND));

        Notes updateNotes = makeNotesEntity(requestNotesDTO,teamMember);


        if(requestNotesDTO.getMemberId() == notes.getTeamMember().getMember().getId()){
            try{
                notesRepository.save(updateNotes);
                linkImagesToNotes(updateNotes.getContent(),updateNotes);
            } catch (Exception e){
                throw new BusinessLogicException(NotesExceptionType.NOTES_SAVE_ERROR);
            }
        } else{
            throw new BusinessLogicException(TeamMemberExceptionType.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public void deleteNotes(Long notesId, Long teamMemberId) {
        Notes notes = notesRepository.findById(notesId).orElseThrow(() ->
                new BusinessLogicException(NotesExceptionType.NOTES_NOT_FOUND));
        if(teamMemberId == notes.getTeamMember().getId()){
            try{
                notesRepository.delete(notes);
            } catch(Exception e){
                throw new BusinessLogicException(NotesExceptionType.NOTES_DELETE_ERROR);
            }
        } else{
            throw new BusinessLogicException(TeamMemberExceptionType.FORBIDDEN);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public URL saveImage(MultipartFile file){
        // 파일명을 업로드 한 날짜로 변환하여 저장
        // S3에서 파일명이 겹치지 않게 하기 위해서
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String current_date = now.format(dateTimeFormatter);

        String originalFileExtension = "";
        String contentType = file.getContentType();

        //확장자 명이 존재하지 않을 경우 처리 X
        if(contentType.contains("image/jpeg") || contentType.contains("image/jpg"))
            originalFileExtension = ".jpg";
        else if(contentType.contains("image/png"))
            originalFileExtension = ".png";


        // 파일명 중복을 피하기 위해 나노초까지 얻어와 지정
        String new_file_name = current_date + System.nanoTime() + originalFileExtension;

        try {
            String fileUrl = new_file_name;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket,new_file_name,file.getInputStream(),metadata);
            URL url = s3Client.getUrl(bucket,fileUrl);
            Image image = Image.builder()
                    .imageName(fileUrl)
                    .imageUrl(String.valueOf(url))
                    .build();
            imageRepository.save(image);
            return url;
        } catch (IOException e) {
            throw new BusinessLogicException(NotesExceptionType.NOTES_SAVE_ERROR);
        }
    }

    // HTML에서 이미지 src를 추출하여서 관계 맺어주는거
    public void linkImagesToNotes(String html, Notes notes){
        List<String> srcList = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            srcList.add(matcher.group(1)); // 이미지 src 속성 값 추출
        }

        // 이미 노트와 관련된 이미지들 가져오기
        List<Image> relatedImages = imageRepository.findByNotes(notes);

        // HTML에 있는 이미지와 이미 노트와 관련된 이미지들을 비교하여 관계 맺기
        for (String src : srcList) {
            Image image = imageRepository.findByImageUrl(src);
            if (image != null && relatedImages.contains(image)) {
                // 이미 노트와 관련된 이미지이면서 HTML에도 있는 경우, 관계 유지
                continue;
            }
            // 이미 노트와 관련된 이미지가 아니거나 HTML에 없는 경우, 새로운 이미지 생성 및 관계 맺기
            imageRepository.save(Image.builder()
                    .imageId(image.getImageId())
                    .imageUrl(image.getImageUrl())
                    .imageName(image.getImageName())
                    .notes(notes)
                    .build());
        }

        // HTML에는 없지만 이미 노트와 관련된 이미지들을 찾아서 끊기
        for (Image image : relatedImages) {
            if (!srcList.contains(image.getImageUrl())) {
                imageRepository.unlinkImages(image.getImageId());
            }
        }
    }

    public ResponseNotesDTO makeResponseNotesDTO(Notes notes){
        ResponseNotesDTO responseNotesDTO;
        if(notes.getNotesId() != null){
            responseNotesDTO = ResponseNotesDTO.builder()
                    .notesId(notes.getNotesId())
                    .title(notes.getTitle())
                    .content(notes.getContent())
                    .createdDate(notes.getCreatedDate())
                    .modifiedDate(notes.getModifiedDate())
                    .date(notes.getDate())
                    .type(NotesType.valueOf(notes.getType()))
                    .summary(notes.getSummary())
                    .build();
        } else {
            responseNotesDTO = ResponseNotesDTO.builder()
                    .title(notes.getTitle())
                    .content(notes.getContent())
                    .createdDate(notes.getCreatedDate())
                    .modifiedDate(notes.getModifiedDate())
                    .date(notes.getDate())
                    .type(NotesType.valueOf(notes.getType()))
                    .summary(notes.getSummary())
                    .build();
        }
        return responseNotesDTO;
    }

    public Notes makeNotesEntity(RequestNotesDTO requestNotesDTO, TeamMember teamMember){
        Notes notes = Notes.builder()
                .notesId(requestNotesDTO.getNotesId())
                .title(requestNotesDTO.getTitle())
                .createdDate(new Date())
                .type(String.valueOf(requestNotesDTO.getType()))
                .date(requestNotesDTO.getDate())
                .modifiedDate(new Date())
                .content(requestNotesDTO.getContent())
                .summary(requestNotesDTO.getSummary())
                .teamMember(teamMember)
                .build();
        return notes;
    }
}
