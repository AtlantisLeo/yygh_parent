package com.whozm.yygh.user.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.common.utils.JwtHelper;
import com.whozm.yygh.model.user.UserInfo;
import com.whozm.yygh.user.service.UserInfoService;
import com.whozm.yygh.vo.user.LoginVo;
import com.whozm.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author HZM
 * @since 2023-01-26
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        Map<String,Object> map = userInfoService.login(loginVo);
        return R.ok().data(map);
    }

    @GetMapping("/info")
    public R getUserInfo(@RequestHeader String token) {
        Long userId = JwtHelper.getUserId(token);
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        return R.ok().data("userInfo",userInfo);
    }

    @PutMapping("/update")
    public R updateUserAuth(@RequestHeader String token,@RequestBody UserAuthVo userAuthVo) {
        Long userId = JwtHelper.getUserId(token);
        userInfoService.updateUserAuth(userId,userAuthVo);
        return R.ok();
    }
}



