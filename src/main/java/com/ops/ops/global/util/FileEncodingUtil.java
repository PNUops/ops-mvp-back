package com.ops.ops.global.util;

import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileEncodingUtil {

    private final FileRepository fileRepository;

    @Async("imageTaskExecutor")
    @Transactional
    public void convertToWebpAndSave(MultipartFile multipartFile, Path webpFilePath, Long fileId) {
        long startTime = System.currentTimeMillis(); // 시작 시간 기록
        try {
            ImmutableImage image = ImmutableImage.loader().fromStream(multipartFile.getInputStream());
            WebpWriter writer = WebpWriter.DEFAULT.withQ(80);

            image.output(writer, webpFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis(); // 종료 시간 기록
        long duration = endTime - startTime; // 실행 시간 계산
        System.out.println("convertToWebpAndSave 실행 시간: " + duration + "ms");

        fileRepository.findById(fileId).ifPresent(file -> file.updateIsWebpConverted(true));
    }

}
