package com.commercecore.api.media.service;

import com.commercecore.api.common.exception.ResourceNotFoundException;
import com.commercecore.api.media.dto.FolderDto;
import com.commercecore.api.media.dto.MediaDto;
import com.commercecore.api.media.entity.Folder;
import com.commercecore.api.media.entity.Media;
import com.commercecore.api.media.repository.FolderRepository;
import com.commercecore.api.media.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MediaServiceImpl implements MediaService {

    private final FolderRepository folderRepository;
    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    public MediaServiceImpl(FolderRepository folderRepository, MediaRepository mediaRepository, S3Service s3Service) {
        this.folderRepository = folderRepository;
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional
    public FolderDto createFolder(String name, UUID parentId) {
        log.info("Creating folder name={}, parentId={}", name, parentId);
        Folder folder = new Folder();
        folder.setName(name);

        if (parentId != null) {
            Folder parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Folder", parentId));
            folder.setParent(parent);
        }

        Folder saved = folderRepository.save(folder);
        return mapFolderToDto(saved);
    }

    @Override
    public List<FolderDto> getRootFolders() {
        return folderRepository.findByParentIsNullOrderByNameAsc().stream()
                .map(this::mapFolderToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaDto> getRootMedia() {
        return mediaRepository.findByFolderIsNullOrderByNameAsc().stream()
                .map(this::mapMediaToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FolderDto getFolderContent(UUID folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", folderId));
        return mapFolderToDto(folder);
    }

    @Override
    @Transactional
    public MediaDto uploadMedia(MultipartFile file, String altText, UUID folderId) throws IOException {
        log.info("Uploading media filename={}, folderId={}", file.getOriginalFilename(), folderId);

        Folder folder = null;
        String folderPath = "";
        if (folderId != null) {
            folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Folder", folderId));
            folderPath = getFolderPath(folder);
        }

        // Upload directly to DO Spaces using S3 Client
        S3Service.UploadResult uploadResult = s3Service.uploadFile(file, folderPath);

        Media media = new Media();
        media.setName(uploadResult.originalFilename());
        media.setAltText(altText);
        media.setKey(uploadResult.key());
        media.setUrl(uploadResult.url());
        media.setSize(uploadResult.size());
        media.setMimeType(uploadResult.mimeType());
        media.setFolder(folder);

        // Detect resolution if file is an image
        if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
            try {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image != null) {
                    media.setWidth(image.getWidth());
                    media.setHeight(image.getHeight());
                }
            } catch (Exception e) {
                log.warn("Failed to read image resolution parameters for media", e);
            }
        }

        Media saved = mediaRepository.save(media);
        return mapMediaToDto(saved);
    }

    @Override
    @Transactional
    public void deleteMedia(UUID id) {
        log.info("Deleting media id={}", id);
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media", id));
        
        // We delete from database. The physical asset stays or gets purged based on lifecycle rules.
        mediaRepository.delete(media);
    }

    @Override
    @Transactional
    public void deleteFolder(UUID id) {
        log.info("Deleting folder id={}", id);
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", id));
        folderRepository.delete(folder);
    }

    @Override
    @Transactional
    public void moveMedia(List<UUID> mediaIds, UUID folderId) {
        log.info("Moving media files count={}, targetFolderId={}", mediaIds.size(), folderId);
        Folder targetFolder = null;
        if (folderId != null) {
            targetFolder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Folder", folderId));
        }

        List<Media> mediaList = mediaRepository.findAllById(mediaIds);
        for (Media media : mediaList) {
            media.setFolder(targetFolder);
        }
        mediaRepository.saveAll(mediaList);
    }

    // ---- Hierarchical Folder Path Resolver ----

    private String getFolderPath(Folder folder) {
        List<String> pathSegments = new ArrayList<>();
        Folder current = folder;
        while (current != null) {
            pathSegments.add(0, current.getName().toLowerCase().replaceAll("[^a-z0-9-]", "-"));
            current = current.getParent();
        }
        return String.join("/", pathSegments);
    }

    // ---- Recursive Mapping Helpers ----

    private FolderDto mapFolderToDto(Folder folder) {
        FolderDto dto = new FolderDto();
        dto.setId(folder.getId());
        dto.setName(folder.getName());
        dto.setParentId(folder.getParent() != null ? folder.getParent().getId() : null);

        if (folder.getSubfolders() != null) {
            dto.setFolders(folder.getSubfolders().stream()
                    .map(this::mapFolderToDto)
                    .collect(Collectors.toList()));
        }

        if (folder.getMedia() != null) {
            dto.setFiles(folder.getMedia().stream()
                    .map(this::mapMediaToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private MediaDto mapMediaToDto(Media media) {
        MediaDto dto = new MediaDto();
        dto.setId(media.getId());
        dto.setName(media.getName());
        dto.setAltText(media.getAltText());
        dto.setKey(media.getKey());
        dto.setUrl(media.getUrl());
        dto.setSize(media.getSize());
        dto.setMimeType(media.getMimeType());
        dto.setFolderId(media.getFolder() != null ? media.getFolder().getId() : null);
        dto.setWidth(media.getWidth());
        dto.setHeight(media.getHeight());
        return dto;
    }

}
