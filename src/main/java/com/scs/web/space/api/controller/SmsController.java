package com.scs.web.space.api.controller;

import com.scs.web.space.api.domain.entity.VerifyNumber;
import com.scs.web.space.api.mapper.UserMapper;
import com.scs.web.space.api.service.RedisService;
import com.scs.web.space.api.util.Result;
import com.scs.web.space.api.util.ResultCode;
import com.scs.web.space.api.util.SmsUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Date;

/**
 * @ClassName SmsController
 * @Description TODO
 * @Author 田震
 * @Date 2019/12/6
 **/
@RestController
@RequestMapping(value = "/api/sms")
public class SmsController {
    @Resource
    private RedisService redisService;
    @Resource
 private UserMapper userMapper;
    private String phone;
    String verifyCode;
    /**
     * 通过短信验证注册
     * @param mobile
     * @return
     */
    @PostMapping(value = "/sendcode")
    public Result getVerifyCode(@RequestParam(value = "mobile") String mobile) throws SQLException {

            if (userMapper.findUserByMobile(mobile) != null) {
                return Result.failure(ResultCode.USER_HAS_EXISTED);
            } else {
                //发送验证码
                verifyCode = SmsUtil.send(mobile);
                VerifyNumber number = new VerifyNumber(mobile, verifyCode, new Date());
                redisService.set(mobile, number);
                return Result.success();
            }
    }

    /**
     * 从Redis中取出这个手机号的验证码
     * 和客户端传过来的验证码比对
     *
     * @param mobile
     * @param verifyCode
     * @return
     */
    @PostMapping(value = "/checkcode")
    public Result checkVerifyCode(@RequestParam("mobile") String mobile, @RequestParam("verifyCode") String verifyCode) {
        VerifyNumber number = redisService.getValue(mobile, VerifyNumber.class);

        //从redis中先取出code，和verifyCode比较
        if (number.getCode().equals(verifyCode)) {
            if (((System.currentTimeMillis() - number.getTime().getTime()) /(1000*60)) <= 5) {

                return Result.success();
            } else {
                return Result.failure(ResultCode.USER_VERIFY_OVERDUE);
            }
        } else {
            return Result.failure(ResultCode.USER_VERIFY_CODE_ERROR);
        }
    }
}