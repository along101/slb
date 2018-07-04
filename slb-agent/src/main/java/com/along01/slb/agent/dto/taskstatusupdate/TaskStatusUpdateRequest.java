package com.along01.slb.agent.dto.taskstatusupdate;

import java.util.List;

import com.along01.slb.agent.dto.taskconfget.TaskAppDto;

import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
   private String ip;
   private List<TaskAppDto> taskApps;
}
