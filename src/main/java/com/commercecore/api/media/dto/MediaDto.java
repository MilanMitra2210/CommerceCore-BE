package com.commercecore.api.media.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MediaDto {

    private UUID id;
    private String name;
    private String altText;
    private String key;
    private String url;
    private Long size;
    private String mimeType;
    private UUID folderId;
    private Integer width;
    private Integer height;

}
