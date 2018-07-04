package com.along101.wormhole.admin.dto.task;

import java.util.List;

import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
   private String ip;
   private List<TaskAppDto> taskApps;
}
