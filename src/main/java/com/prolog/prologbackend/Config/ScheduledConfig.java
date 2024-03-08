package com.prolog.prologbackend.Config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.prolog.prologbackend.Notes.Domain.Image;
import com.prolog.prologbackend.Notes.Repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledConfig {

    private final ImageRepository imageRepository;

    private final AmazonS3 s3Client;

    @Value("${S3Bucket}")
    private String bucket;

    @Scheduled(cron = "0 54 13 * * ?") // 매일 새벽 12시에 실행
    @Transactional(rollbackFor = Exception.class)
    public void imageDelete() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayFormatted = today.format(formatter);
        String yesterdayFormatted = yesterday.format(formatter);

        // 오늘과 어제의 이미지를 제외한 나머지 중 notes_id가 null인 이미지 가져오기
        List<Image> imagesToDelete = imageRepository.findByNotesIsNullAndImageNameNotStartingWith(todayFormatted, yesterdayFormatted);

        for (Image image : imagesToDelete) {
            try {
                s3Client.deleteObject(new DeleteObjectRequest(bucket, image.getImageName()));
                imageRepository.delete(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

