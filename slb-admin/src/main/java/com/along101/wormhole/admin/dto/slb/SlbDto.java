package com.along101.wormhole.admin.dto.slb;

import com.along.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import lombok.Data;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Data
public class SlbDto extends BaseDto {
    private Long id;
    private String name;
    private String nginxConf;
    private String nginxSbin;
    private List<SlbServerDto> slbServers;
}
