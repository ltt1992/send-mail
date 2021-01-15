package com.example.mail.interfaces;

import com.example.mail.response.dto.TaskInfoDto;
import com.example.mail.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class OperateController {

    @Autowired
    private TaskInfoService taskInfoService;

    @GetMapping({"/index","/"})
    public String taskInfoList(Model model){
        List<TaskInfoDto> taskInfoList = taskInfoService.taskInfoList();
        model.addAttribute("taskInfoList",taskInfoList);
        model.addAttribute("taskInfoDto",new TaskInfoDto());
        return "task_info_list";
    }

    @PostMapping("/create")
    public String createTaskInfo(@ModelAttribute TaskInfoDto taskInfoDto){
        taskInfoService.createTask(taskInfoDto);
        return "redirect:index";
    }

    @PostMapping("/del_task")
    public String delTaskInfo(String taskId){
        taskInfoService.delTaskInfo(taskId);
        return "redirect:index";
    }
}
