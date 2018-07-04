package com.along101.wormhole.admin.dto.slb;

import com.along.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import lombok.Data;

import java.util.Date;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Data
public class SlbServerDto extends BaseDto {
    private Long id;
    private Long slbId;
    private String ip;
    private String hostName;
    private Date heartTime;
}
