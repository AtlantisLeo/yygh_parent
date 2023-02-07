package com.whozm.yygh.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.common.utils.JwtHelper;
import com.whozm.yygh.enums.AuthStatusEnum;
import com.whozm.yygh.enums.StatusEnum;
import com.whozm.yygh.model.user.Patient;
import com.whozm.yygh.model.user.UserInfo;
import com.whozm.yygh.user.mapper.UserInfoMapper;
import com.whozm.yygh.user.service.PatientService;
import com.whozm.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whozm.yygh.vo.user.LoginVo;
import com.whozm.yygh.vo.user.UserAuthVo;
import com.whozm.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HZM
 * @since 2023-01-26
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PatientService patientService;
    @Override
    public Map<String, Object> login(LoginVo loginVo) {

        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(20001,"手机号或验证码错误");
        }

        String redisCode = stringRedisTemplate.opsForValue().get(phone);
        if (StringUtils.isEmpty(redisCode) || !code.equals(redisCode)){
            throw new YyghException(20001,"验证码错误");
        }
        String openid = loginVo.getOpenid();
        UserInfo userInfo = null;
        System.out.println("-------------------"+openid);
        if (StringUtils.isEmpty(openid)){
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone",phone);
            userInfo = baseMapper.selectOne(queryWrapper);
            if (userInfo == null){
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }else{
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone",phone);
            userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null){
                QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("openid",openid);
                userInfo = baseMapper.selectOne(queryWrapper);
                userInfo.setPhone(phone);
                baseMapper.updateById(userInfo);
            }else {
                QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("openid",openid);
                UserInfo userInfo1 = baseMapper.selectOne(queryWrapper);
                userInfo.setNickName(userInfo1.getNickName());
                userInfo.setOpenid(userInfo1.getOpenid());
                baseMapper.updateById(userInfo);
                baseMapper.deleteById(userInfo1.getId());
            }
        }
        if(userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户锁定中");
        }

        Map<String, Object> result = new HashMap<>();
        String nickName = userInfo.getNickName();
        if (StringUtils.isEmpty(nickName)){
            nickName = userInfo.getPhone();
        }
        result.put("name",nickName);
        String token = JwtHelper.createToken(userInfo.getId(), nickName);
        result.put("token",token);

        return result;
    }

    @Override
    public UserInfo getUserInfo(Long userId) {

        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        return userInfo;
    }

    @Override
    public void updateUserAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setUpdateTime(new Date());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }

    @Override
    public Page<UserInfo> getUserInfoPage(Integer pageNum, Integer limit, UserInfoQueryVo userInfoQueryVo) {

        Page<UserInfo> page = new Page<>(pageNum,limit);
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        String keyword = userInfoQueryVo.getKeyword();
        if (!StringUtils.isEmpty(keyword)){
            wrapper.like("name",keyword).or().eq("phone",keyword);
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getStatus())){
            wrapper.eq("status",userInfoQueryVo.getStatus());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getAuthStatus())){
            wrapper.eq("auth_status",userInfoQueryVo.getAuthStatus());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getAuthStatus())){
            wrapper.eq("auth_status",userInfoQueryVo.getAuthStatus());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeBegin())){
            wrapper.ge("create_time",userInfoQueryVo.getCreateTimeBegin());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeEnd())){
            wrapper.le("create_time",userInfoQueryVo.getCreateTimeEnd());
        }
        Page<UserInfo> userInfoPage = baseMapper.selectPage(page, wrapper);
        userInfoPage.getRecords().stream().forEach(item->{
            this.packageUserInfo(item);
        });

        return userInfoPage;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == 0 || status == 1){
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> detail(Long id) {
        UserInfo userInfo = baseMapper.selectById(id);
        List<Patient> patients = patientService.selectList(id);
        Map<String, Object> map = new HashMap<>(2);
        map.put("userInfo",userInfo);
        map.put("patients",patients);
        return map;
    }

    @Override
    public void updateAuthStatus(Long id, Integer authStatus) {
        if (authStatus == 2 || authStatus == -1){
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    private void packageUserInfo(UserInfo item) {

        Integer status = item.getStatus();
        Integer authStatus = item.getAuthStatus();
        item.getParam().put("statusString", StatusEnum.getStatusStringByStatus(status));
        item.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(authStatus));
    }
}
