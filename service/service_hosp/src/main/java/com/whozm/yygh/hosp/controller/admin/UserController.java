package com.whozm.yygh.hosp.controller.admin;

import com.whozm.yygh.common.result.R;
import com.whozm.yygh.model.acl.User;
import org.springframework.web.bind.annotation.*;

/**
 * @author HZM
 * @date 2023/1/17
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @PostMapping("/login")
    public R login(@RequestBody User user){
        return R.ok().data("token","admin-token");
    }

    @GetMapping("/info")
    public R info(@RequestParam("token") String token){
        return R.ok().data("roles","[admin]")
                .data("introduction","I am a super administrator")
                .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name","后台管理系统");

    }
}
