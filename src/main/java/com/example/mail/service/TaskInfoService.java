package com.example.mail.service;

import com.example.mail.response.dto.TaskInfoDto;

import java.util.List;

/**
 * @author liutongtong
 */
public interface TaskInfoService {
    /**
     * 创建提示任务信息
     * @param dto
     */
    void createTask(TaskInfoDto dto);

    /**
     * 获取任务信息列表
     * @return
     */
    List<TaskInfoDto> taskInfoList();

    /**
     * 删除任务
     * @param taskId
     */
    void delTaskInfo(String taskId);

    /**
     * 定时任务
     */
    void scheduling();
}
