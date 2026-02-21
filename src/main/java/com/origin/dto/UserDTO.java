package com.origin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String password;
    private String email;
    private String code;
    private String avatar;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
    private Integer status;



}
