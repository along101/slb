package com.along01.slb.agent.dto.taskconfget;

import lombok.Data;

@Data
public class TaskConfGetRequest {
  private String ip;
  private long lastTaskId;
}
