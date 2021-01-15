package com.example.mail.service.impl;

import com.example.mail.entity.TaskInfo;
import com.example.mail.mapper.TaskInfoMapper;
import com.example.mail.response.dto.TaskInfoDto;
import com.example.mail.service.TaskInfoService;
import com.example.mail.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TaskInfoServiceImpl implements TaskInfoService {
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void createTask(TaskInfoDto dto) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskName(dto.getTaskName());
        taskInfo.setTaskDetail(dto.getTaskDetail());
        taskInfo.setFirstSignTime(DateUtils.strToLocalDate(dto.getFirstSignTime(),DateUtils.DATE_FORMATTER));
        if(StringUtils.isNotEmpty(dto.getSecondSignTime())){
            taskInfo.setSecondSignTime(DateUtils.strToLocalDate(dto.getSecondSignTime(),DateUtils.DATE_FORMATTER));
        }
        taskInfoMapper.insert(taskInfo);
    }

    @Override
    public List<TaskInfoDto> taskInfoList() {
        final List<TaskInfo> taskInfoList = taskInfoMapper.selectList(null);
        if(null != taskInfoList && !taskInfoList.isEmpty()){
            List<TaskInfoDto> taskInfoDtoList = new ArrayList<>();
            TaskInfoDto dto = null;
            for (TaskInfo taskInfo : taskInfoList) {
                dto = new TaskInfoDto();
                dto.setId(taskInfo.getId());
                dto.setTaskName(taskInfo.getTaskName());
                dto.setTaskDetail(taskInfo.getTaskDetail());
                dto.setFirstSignTime(DateUtils.localDateToStr(taskInfo.getFirstSignTime(),DateUtils.DATE_FORMATTER));
                dto.setFirstPromptTime(DateUtils.localDateToStr(DateUtils.localDatePlusDay(taskInfo.getFirstSignTime(),-60),DateUtils.DATE_FORMATTER));
                if(null != taskInfo.getSecondSignTime()){
                    dto.setSecondSignTime(DateUtils.localDateToStr(taskInfo.getSecondSignTime(),DateUtils.DATE_FORMATTER));
                    dto.setSecondPromptTime(DateUtils.localDateToStr(DateUtils.localDatePlusDay(taskInfo.getSecondSignTime(),-60),DateUtils.DATE_FORMATTER));
                }
                taskInfoDtoList.add(dto);
            }
            return taskInfoDtoList;
        }
        return Collections.emptyList();
    }

    @Override
    public void delTaskInfo(String taskId) {
        taskInfoMapper.deleteById(taskId);
    }

    /**
     * 每隔5分钟执行一次==
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Override
    public void scheduling() {
        final List<TaskInfo> taskInfoList = taskInfoMapper.selectList(null);
        if(null != taskInfoList && !taskInfoList.isEmpty()){
            SimpleMailMessage message = new SimpleMailMessage();
            LocalDate now = LocalDate.now();
            LocalDate secondPromptTime = null;
            for (TaskInfo taskInfo : taskInfoList){
                final LocalDate firstPromptTime = DateUtils.localDatePlusDay(taskInfo.getFirstSignTime(), -60);
                if(null != taskInfo.getSecondSignTime()){
                    secondPromptTime = DateUtils.localDatePlusDay(taskInfo.getSecondSignTime(), -60);
                }
                if(firstPromptTime.compareTo(now) == 0){
                    log.info("发送邮件》》》》FIRST");
                    message.setText(String.format("我司员工%s于%s日合同到期，请及时处理！",taskInfo.getTaskName(),DateUtils.localDateToStr(taskInfo.getFirstSignTime(),DateUtils.DATE_FORMATTER)));
                }
                if(null != secondPromptTime && secondPromptTime.compareTo(now) == 0){
                    log.info("发送邮件》》》》SECOND");
                    message.setText(String.format("我司员工%s于%s日合同到期，请及时处理！",taskInfo.getTaskName(),DateUtils.localDateToStr(taskInfo.getSecondSignTime(),DateUtils.DATE_FORMATTER)));
                }
                message.setFrom("373827048@qq.com");
                message.setTo("2623657294@qq.com");
                message.setSubject("主题：员工续约邮件提醒");
                if(StringUtils.isNotEmpty(message.getText())){
                    mailSender.send(message);
                    log.info("Success");
                }
            }
        }
    }
}
