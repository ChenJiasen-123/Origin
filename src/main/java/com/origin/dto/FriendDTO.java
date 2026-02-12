package com.origin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class FriendDTO {
    private String name;
    private Integer age;
    private Long number;
    private LocalDate birthday;
    private String note;
    private Integer howLongForBirthday;
    private List<String> tags;
    private String hometown;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModified;
}
