package com.robosoft.admin.login.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubCategory {
    private Integer categoryId;
    private Integer subCategoryId;
    private String subCategoryName;
}
