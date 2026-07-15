package com.commercecore.api.cms.mapper;

import com.commercecore.api.cms.dto.CMSPageRequest;
import com.commercecore.api.cms.dto.CMSPageResponse;
import com.commercecore.api.cms.entity.CMSPage;
import com.commercecore.api.common.mapper.ContentBlockMapper;
import com.commercecore.api.common.mapper.SeoMapper;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for CMSPage entity and DTO conversions.
 */
@Mapper(componentModel = "spring", uses = {SeoMapper.class, ContentBlockMapper.class})
public interface CMSPageMapper {

    CMSPageResponse toResponse(CMSPage page);

    CMSPage toEntity(CMSPageRequest request);

}
