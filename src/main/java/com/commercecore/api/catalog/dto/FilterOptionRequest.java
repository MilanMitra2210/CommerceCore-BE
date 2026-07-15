package com.commercecore.api.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FilterOptionRequest {

    private UUID id;
    private String label;
    private String value;
    private int displayOrder;
    private String subGroup;
    private String colorCode;
    private UUID mediaId;
    private boolean showInNavbar;

}
