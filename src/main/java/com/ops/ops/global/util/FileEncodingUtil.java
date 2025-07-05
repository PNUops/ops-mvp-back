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
        try {
            ImmutableImage image = ImmutableImage.loader().fromStream(multipartFile.getInputStream());
            WebpWriter writer = WebpWriter.DEFAULT.withQ(80);

            image.output(writer, webpFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileRepository.findById(fileId).ifPresent(file -> file.updateIsWebpConverted(true));
    }

}
