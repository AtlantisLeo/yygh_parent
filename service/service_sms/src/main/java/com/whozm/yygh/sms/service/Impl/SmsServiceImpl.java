package com.whozm.yygh.sms.service.Impl;

import com.whozm.yygh.sms.service.SmsService;
import com.whozm.yygh.sms.utils.HttpUtils;
import com.whozm.yygh.sms.utils.RandomUtil;
import com.whozm.yygh.vo.msm.MsmVo;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author HZM
 * @date 2023/1/26
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean sendCode(String phone) {

        String redisCode = stringRedisTemplate.opsForValue().get(phone);

        if (!StringUtils.isEmpty(redisCode)){
            return true;
        }
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        String fourBitRandom = RandomUtil.getFourBitRandom();
        bodys.put("content", "code:"+fourBitRandom);
        bodys.put("phone_number", phone);
        bodys.put("template_id", "TPL_0000");

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            stringRedisTemplate.opsForValue().set(phone,fourBitRandom, 1,TimeUnit.DAYS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void sendMessage(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        System.out.println("----------发送短信提示成功");
    }
}
