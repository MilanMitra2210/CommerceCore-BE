package com.commercecore.api.media.controller;

import com.commercecore.api.common.dto.ApiResponse;
import com.commercecore.api.media.dto.FolderDto;
import com.commercecore.api.media.dto.MediaDto;
import com.commercecore.api.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.commercecore.api.media.dto.BulkMoveRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing endpoints for folders hierarchy lookups and spaces file uploads.
 */
@RestController
@RequestMapping("/uploads")
@Tag(name = "Media Asset Management", description = "Endpoints for managing folders and uploading file assets to DigitalOcean Spaces")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping
    @Operation(summary = "Get media assets", description = "Returns files and subfolders within a specific directory")
    public ResponseEntity<ApiResponse<FolderDto>> getMedia(
            @RequestParam(name = "folder_id", required = false) UUID folderId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        
        FolderDto folder;
        if (folderId != null) {
            folder = mediaService.getFolderContent(folderId);
        } else {
            // Map top-level elements to an anonymous root folder
            folder = new FolderDto();
            folder.setName("Root");
            folder.setFolders(mediaService.getRootFolders());
            folder.setFiles(mediaService.getRootMedia());
        }
        return ResponseEntity.ok(ApiResponse.success("Media content retrieved successfully", folder));
    }

    @GetMapping("/folders/all")
    @Operation(summary = "Get root directories")
    public ResponseEntity<ApiResponse<List<FolderDto>>> getRootFolders() {
        List<FolderDto> folders = mediaService.getRootFolders();
        return ResponseEntity.ok(ApiResponse.success("Root folders retrieved successfully", folders));
    }

    @GetMapping("/folders/{folderId}")
    @Operation(summary = "Get folder contents")
    public ResponseEntity<ApiResponse<FolderDto>> getFolderContent(@PathVariable UUID folderId) {
        FolderDto folder = mediaService.getFolderContent(folderId);
        return ResponseEntity.ok(ApiResponse.success("Folder contents retrieved successfully", folder));
    }

    @PostMapping("/folders")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create directory")
    public ResponseEntity<ApiResponse<FolderDto>> createFolder(
            @RequestParam String name,
            @RequestParam(name = "parent_id", required = false) UUID parentId) {
        FolderDto folder = mediaService.createFolder(name, parentId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Folder created successfully", folder));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload file asset")
    public ResponseEntity<ApiResponse<MediaDto>> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "alt_text", required = false) String altText,
            @RequestParam(name = "folder_id", required = false) UUID folderId) throws IOException {
        
        MediaDto media = mediaService.uploadMedia(file, altText, folderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Media file uploaded successfully", media));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete media file")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable UUID id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.ok(ApiResponse.success("Media file record deleted successfully"));
    }

    @DeleteMapping("/folders/{folderId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete directory")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(@PathVariable UUID folderId) {
        mediaService.deleteFolder(folderId);
        return ResponseEntity.ok(ApiResponse.success("Folder deleted successfully"));
    }

    @PatchMapping("/bulk-move")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Bulk move media files to a folder")
    public ResponseEntity<ApiResponse<Void>> bulkMoveMedia(
            @RequestBody BulkMoveRequest request) {
        mediaService.moveMedia(request.getMediaIds(), request.getFolderId());
        return ResponseEntity.ok(ApiResponse.success("Media files moved successfully"));
    }

}
