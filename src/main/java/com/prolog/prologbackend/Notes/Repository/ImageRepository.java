package com.prolog.prologbackend.Notes.Repository;

import com.prolog.prologbackend.Notes.Domain.Image;
import com.prolog.prologbackend.Notes.Domain.Notes;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findByImageUrl(String imageUrl);

    List<Image> findByNotes(Notes notes);

    @Modifying
    @Transactional
    @Query(value = "UPDATE image SET notes_id = NULL WHERE image_id = ?1", nativeQuery = true)
    void unlinkImages(Long imageId);

    @Query("SELECT i FROM Image i WHERE i.notes IS NULL AND (i.imageName NOT LIKE CONCAT(:todayFormatted, '%') AND i.imageName NOT LIKE CONCAT(:yesterdayFormatted, '%'))")
    List<Image> findByNotesIsNullAndImageNameNotStartingWith(@Param(value = "todayFormatted") String todayFormatted, @Param(value = "yesterdayFormatted") String yesterdayFormatted);

    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.notes IN (SELECT n FROM Notes n WHERE n.teamMember.id IN :teamMemberIds)")
    void deleteImagesByTeamMemberIds(List<Long> teamMemberIds);

    @Transactional
    @Modifying
    @Query("SELECT i FROM Image i WHERE i.notes IN (SELECT n FROM Notes n WHERE n.teamMember.id IN :teamMemberIds)")
    List<Image> findImagesByTeamMemberIds(List<Long> teamMemberIds);
}
