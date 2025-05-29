package com.ops.ops.modules.file.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ThumbnailRequest {
    private MultipartFile image;
}
