package com.whozm.yygh.sms.service;

import com.whozm.yygh.vo.msm.MsmVo;

/**
 * @author HZM
 * @date 2023/1/26
 */
public interface SmsService {
    boolean sendCode(String phone);

    void sendMessage(MsmVo msmVo);
}
