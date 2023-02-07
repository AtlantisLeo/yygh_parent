package com.whozm.yygh.user.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.common.utils.JwtHelper;
import com.whozm.yygh.model.user.UserInfo;
import com.whozm.yygh.user.prop.WeiXinProperties;
import com.whozm.yygh.user.service.UserInfoService;
import com.whozm.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/27
 */
@Controller
@RequestMapping("/user/userinfo/wx")
public class WeiXinController {

    @Autowired
    private WeiXinProperties weiXinProperties;
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/param")
    @ResponseBody
    public R getWeiXinLoginParam() throws UnsupportedEncodingException {
        Map<String,Object> map = new HashMap<>();
        String appid = weiXinProperties.getAppid();
        String encode = URLEncoder.encode(weiXinProperties.getRedirecturl(), "UTF-8");
        map.put("appid",appid);
        map.put("scope","snsapi_login");
        map.put("redirecturl",encode);
        map.put("state",System.currentTimeMillis()+"");
        return R.ok().data(map);
    }

    @GetMapping("/callback")
    public String callback(String code,String state) throws Exception {

        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String format = String.format(baseAccessTokenUrl.toString(), weiXinProperties.getAppid(), weiXinProperties.getAppsecret(), code);
        String result = HttpClientUtils.get(format);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String access_token = jsonObject.getString("access_token");
        String openid = jsonObject.getString("openid");
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        if (userInfo==null){
            StringBuffer baseUserInfoUrl = new StringBuffer()
                    .append("https://api.weixin.qq.com/sns/userinfo")
                    .append("?access_token=%s")
                    .append("&openid=%s");
            String userInfoUrl = String.format(baseUserInfoUrl.toString(), access_token, openid);
            String s = HttpClientUtils.get(userInfoUrl);
            JSONObject jsonObject1 = JSONObject.parseObject(s);
            userInfo = new UserInfo();
            userInfo.setOpenid(openid);
            String nickname = jsonObject1.getString("nickname");
            userInfo.setNickName(nickname);
            userInfoService.save(userInfo);
        }else if(userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户锁定中");
        }
        Map<String, String> map = new HashMap<>();
        String name = userInfo.getNickName();
        if (StringUtils.isEmpty(userInfo.getPhone())){
            map.put("openid",openid);
        }else {
            map.put("openid","");
        }
        map.put("name",name);
        String token = JwtHelper.createToken(userInfo.getId(),name);
        map.put("token",token);

        return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
    }
}
