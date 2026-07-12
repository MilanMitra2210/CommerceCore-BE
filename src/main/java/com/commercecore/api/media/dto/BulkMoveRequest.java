package com.commercecore.api.media.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BulkMoveRequest {

    @JsonProperty("media_ids")
    private List<UUID> mediaIds;

    @JsonProperty("folder_id")
    private UUID folderId;

}
