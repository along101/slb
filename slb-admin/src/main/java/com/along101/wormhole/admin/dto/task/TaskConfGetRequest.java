package com.along101.wormhole.admin.dto.task;

import lombok.Data;

@Data
public class TaskConfGetRequest {
  private String ip;
  private Long lastTaskId;
}
