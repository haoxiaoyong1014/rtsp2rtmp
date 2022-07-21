package com.xuhuihealth.rtmp.server.entities;

import lombok.Data;

import java.io.Serializable;

/**
 * @author haoxiaoyong
 * @date created at 下午1:05 on 2022/3/2
 * @github https://github.com/haoxiaoyong1014
 * @blog www.haoxiaoyong.cn
 */
@Data
public class RtspToRtmpRequest implements Serializable {

   private String streamUrl;
   private Long i;
}
