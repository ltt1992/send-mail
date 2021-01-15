package com.example.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@TableName(value = "task_info")
@Data
public class TaskInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String taskName;
    private String taskDetail;
    private LocalDate firstSignTime;
    private LocalDate secondSignTime;
}
