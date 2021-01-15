package com.example.mail.response.dto;

import lombok.Data;

@Data
public class TaskInfoDto {
    private Integer id;
    private String taskName;
    private String taskDetail;
    private String firstSignTime;
    private String secondSignTime;
    private String firstPromptTime;
    private String secondPromptTime;
}
