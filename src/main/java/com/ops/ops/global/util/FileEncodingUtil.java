package com.ops.ops.global.util;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileEncodingUtil {

    @Async("imageTaskExecutor")
    public void convertToWebpAndSave(MultipartFile multipartFile, Path webpFilePath) {

        try {
            ImmutableImage image = ImmutableImage.loader().fromStream(multipartFile.getInputStream());
            WebpWriter writer = WebpWriter.DEFAULT.withQ(80);

            image.output(writer, webpFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
