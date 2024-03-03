package com.prolog.prologbackend.Notes.Repository;

import com.prolog.prologbackend.Notes.Domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
