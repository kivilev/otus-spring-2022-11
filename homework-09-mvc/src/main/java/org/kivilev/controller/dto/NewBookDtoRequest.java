/*
 * Copyright (c) 2023.
 * Author: Kivilev Denis <kivilev.d@gmail.com>
 */

package org.kivilev.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@AllArgsConstructor
public class NewBookDtoRequest {
    @NotBlank
    private String title;
    @NotNull
    @Digits(integer = 4, fraction = 0)
    private Integer createdYear;
    @NotNull
    @Positive
    private Long authorId;
    @NotNull
    @Positive
    private Long genreId;
}
