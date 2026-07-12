package com.commercecore.api.media.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FolderDto {

    private UUID id;
    private String name;
    private UUID parentId;
    private List<FolderDto> folders;
    private List<MediaDto> files;

}
