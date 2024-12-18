package com.example.peaceful_land.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data @Getter @Setter @Builder
public class ChangePostThumbnailRequest {
    private Long post_id;
    private MultipartFile image;
}
