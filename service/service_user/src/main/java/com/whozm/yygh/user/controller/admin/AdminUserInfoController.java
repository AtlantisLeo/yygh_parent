package com.whozm.yygh.user.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.model.user.UserInfo;
import com.whozm.yygh.user.service.UserInfoService;
import com.whozm.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/29
 */
@RestController
@RequestMapping("/adminuser/userinfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/{pageNum}/{limit}")
    public R getUserInfoPage(@PathVariable Integer pageNum, @PathVariable Integer limit, UserInfoQueryVo userInfoQueryVo){
       Page<UserInfo> page =  userInfoService.getUserInfoPage(pageNum,limit,userInfoQueryVo);
        return R.ok().data("total",page.getTotal()).data("list",page.getRecords());

    }

    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable Long id, @PathVariable Integer status){

        userInfoService.updateStatus(id,status);
        return R.ok();

    }

    @PutMapping("/auth/{id}/{authStatus}")
    public R updateAuthStatus(@PathVariable Long id, @PathVariable Integer authStatus){
        userInfoService.updateAuthStatus(id,authStatus);
        return R.ok();

    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id){
        Map<String,Object> userInfo = userInfoService.detail(id);
        return R.ok().data(userInfo);
    }
}
