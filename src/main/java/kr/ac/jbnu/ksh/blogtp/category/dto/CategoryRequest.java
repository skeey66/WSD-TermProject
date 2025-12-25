package kr.ac.jbnu.ksh.blogtp.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(min = 1, max = 80) String name
) {}
