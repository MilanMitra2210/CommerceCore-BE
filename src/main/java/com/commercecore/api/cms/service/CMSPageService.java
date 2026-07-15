package com.commercecore.api.cms.service;

import com.commercecore.api.cms.dto.CMSPageRequest;
import com.commercecore.api.cms.dto.CMSPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for business operations on CMS pages.
 */
public interface CMSPageService {

    CMSPageResponse createCMSPage(CMSPageRequest request);

    CMSPageResponse updateCMSPage(UUID id, CMSPageRequest request);

    Page<CMSPageResponse> getCMSPages(String search, String status, Pageable pageable);

    CMSPageResponse getCMSPageById(UUID id);

    CMSPageResponse getCMSPageBySlug(String slug);

    void deleteCMSPage(UUID id);

}
