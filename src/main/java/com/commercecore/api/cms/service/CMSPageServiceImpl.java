package com.commercecore.api.cms.service;

import com.commercecore.api.cms.dto.CMSPageRequest;
import com.commercecore.api.cms.dto.CMSPageResponse;
import com.commercecore.api.cms.entity.CMSPage;
import com.commercecore.api.cms.mapper.CMSPageMapper;
import com.commercecore.api.cms.repository.CMSPageRepository;
import com.commercecore.api.common.dto.ContentBlockDto;
import com.commercecore.api.common.entity.ContentBlock;
import com.commercecore.api.common.entity.SeoMetadata;
import com.commercecore.api.common.exception.DuplicateResourceException;
import com.commercecore.api.common.exception.ResourceNotFoundException;
import com.commercecore.api.common.mapper.SeoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CMSPageServiceImpl implements CMSPageService {

    private final CMSPageRepository cmsPageRepository;
    private final CMSPageMapper cmsPageMapper;
    private final SeoMapper seoMapper;

    @Override
    @Transactional
    public CMSPageResponse createCMSPage(CMSPageRequest request) {
        if (cmsPageRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("CMS Page with slug " + request.getSlug() + " already exists");
        }

        CMSPage page = cmsPageMapper.toEntity(request);
        
        // Associate nested ContentBlocks with page ID on initial save
        if (page.getContentBlocks() != null) {
            for (ContentBlock block : page.getContentBlocks()) {
                block.setPageId(page.getId());
            }
        }

        CMSPage saved = cmsPageRepository.save(page);
        return cmsPageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CMSPageResponse updateCMSPage(UUID id, CMSPageRequest request) {
        CMSPage page = cmsPageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CMSPage", id));

        if (!page.getSlug().equals(request.getSlug()) && cmsPageRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("CMS Page with slug " + request.getSlug() + " already exists");
        }

        page.setName(request.getName());
        page.setSlug(request.getSlug());
        page.setActive(request.isActive());

        // Update SEO Metadata
        if (request.getSeo() != null) {
            if (page.getSeo() == null) {
                page.setSeo(seoMapper.toEntity(request.getSeo()));
            } else {
                SeoMetadata seo = page.getSeo();
                seo.setMetaTitle(request.getSeo().getMetaTitle());
                seo.setMetaDescription(request.getSeo().getMetaDescription());
                seo.setMetaKeywords(request.getSeo().getMetaKeywords());
                seo.setMetaRobots(request.getSeo().getMetaRobots());
                seo.setMetaImageId(request.getSeo().getMetaImageId());
            }
        } else {
            page.setSeo(null);
        }

        // Update Content Blocks
        page.getContentBlocks().clear();
        if (request.getContentBlocks() != null) {
            for (ContentBlockDto dto : request.getContentBlocks()) {
                ContentBlock block = new ContentBlock();
                block.setBlockKey(dto.getBlockKey());
                block.setContent(dto.getContent());
                block.setPageId(page.getId());
                page.getContentBlocks().add(block);
            }
        }

        CMSPage updated = cmsPageRepository.save(page);
        return cmsPageMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CMSPageResponse> getCMSPages(String search, String status, Pageable pageable) {
        Specification<CMSPage> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));

            if (search != null && !search.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
            }

            if (status != null && !status.trim().isEmpty()) {
                if ("active".equalsIgnoreCase(status)) {
                    predicates.add(cb.equal(root.get("isActive"), true));
                } else if ("draft".equalsIgnoreCase(status) || "inactive".equalsIgnoreCase(status)) {
                    predicates.add(cb.equal(root.get("isActive"), false));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return cmsPageRepository.findAll(spec, pageable).map(cmsPageMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CMSPageResponse getCMSPageById(UUID id) {
        CMSPage page = cmsPageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CMSPage", id));
        return cmsPageMapper.toResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public CMSPageResponse getCMSPageBySlug(String slug) {
        CMSPage page = cmsPageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("CMSPage", "slug", slug));
        return cmsPageMapper.toResponse(page);
    }

    @Override
    @Transactional
    public void deleteCMSPage(UUID id) {
        CMSPage page = cmsPageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CMSPage", id));
        
        // Soft delete
        page.setDeleted(true);
        cmsPageRepository.save(page);
    }
}
