package com.whozm.yygh.user.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whozm.yygh.model.user.UserInfo;
import com.whozm.yygh.vo.user.LoginVo;
import com.whozm.yygh.vo.user.UserAuthVo;
import com.whozm.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author HZM
 * @since 2023-01-26
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo getUserInfo(Long userId);

    void updateUserAuth(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo> getUserInfoPage(Integer pageNum, Integer limit, UserInfoQueryVo userInfoQueryVo);

    void updateStatus(Long id, Integer status);

    Map<String, Object> detail(Long id);

    void updateAuthStatus(Long id, Integer authStatus);
}
