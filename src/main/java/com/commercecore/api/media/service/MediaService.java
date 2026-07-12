package com.commercecore.api.media.service;

import com.commercecore.api.media.dto.FolderDto;
import com.commercecore.api.media.dto.MediaDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MediaService {

    FolderDto createFolder(String name, UUID parentId);

    List<FolderDto> getRootFolders();

    List<MediaDto> getRootMedia();

    FolderDto getFolderContent(UUID folderId);

    MediaDto uploadMedia(MultipartFile file, String altText, UUID folderId) throws IOException;

    void deleteMedia(UUID id);

    void deleteFolder(UUID id);

    void moveMedia(List<UUID> mediaIds, UUID folderId);

}
