package com.commercecore.api.media.repository;

import com.commercecore.api.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findByFolderIdOrderByNameAsc(UUID folderId);

    List<Media> findByFolderIsNullOrderByNameAsc();

}
