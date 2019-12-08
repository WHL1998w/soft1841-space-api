package com.scs.web.space.api.service.impl;
import com.scs.web.space.api.domain.dto.UserDto;
import com.scs.web.space.api.domain.entity.User;
import com.scs.web.space.api.domain.vo.UserVo;
import com.scs.web.space.api.mapper.UserMapper;
import com.scs.web.space.api.service.UserService;
import com.scs.web.space.api.util.Result;
import com.scs.web.space.api.util.ResultCode;
import com.scs.web.space.api.util.SmsUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author
 * @Date 2019/12/1
 **/
@Service
public class UserServiceImpl implements UserService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    /**
     * 登录（成功）
     * @param mobile
     * @param password
     * @return
     */
    @Override
    public Result login(String mobile, String password) {

        User admin = null;
        try {
            admin = userMapper.findUserByMobile(mobile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (admin != null) {
            if (DigestUtils.md5Hex(password).equals(admin.getPassword())) {
                return Result.success(admin);

            } else {  //记录存在，密码输入错误
                return Result.failure(ResultCode.USER_PASSWORD_ERROR);
            }
        } else {  //账号不存在
            return Result.failure(ResultCode.USER_ACCOUNT_ERROR);
        }
    }

    /**
     * 注册
     *
     * @param dto
     * @return
     */
    @Override
    public Result signUp(User dto) {
        User user;
        try {
            user = userMapper.findUserByMobile(dto.getMobile());
        } catch (SQLException e) {
            logger.error("根据手机号查询用户出现异常");
            return Result.failure(ResultCode.USER_SIGN_UP_FAIL);
        }
        //用户手机号已经存在
        if (user != null) {
            return Result.failure(ResultCode.USER_HAS_EXISTED);
        } else {
            try {
                User user1 = new User();
                user1.setMobile(dto.getMobile());
                user1.setPassword(dto.getPassword());
                user1.setEmail(dto.getEmail());
                user1.setBirthday(new Date(2000-01-15));
                user1.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
                SmsUtil.send(dto.getMobile());
                userMapper.insertUser(user1);
            } catch (SQLException e) {
                logger.error("新增用户出现异常");
                return Result.failure(ResultCode.USER_SIGN_UP_FAIL);
            }
        }
        return Result.success();
    }

    /**
     * 根据昵称模糊查询(成功)
     *
     * @return
     */
    @Override
    public List<User> findUserByNickName(String key_name) {
        List<User> userlist = null;
        System.out.println(key_name);
        try {
            userlist = userMapper.findUserByNickName(key_name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userlist;
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @Override
    public int updateUser(User user) {
        int n = 0;
        try {
            n=userMapper.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n;
    }

    @Override
    public Result findUserByMobile(String mobile) {
        User user=null;
        try {
            user=userMapper.findUserByMobile(mobile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Result.success(user);
    }

    @Override
    public Result selectAll(int id) {
        UserVo userVo = new UserVo();
        try {
            userVo = userMapper.getUserById(id);
        } catch (SQLException e) {
            logger.error("查询用户个人动态异常");
        }
        if(userVo != null){
            return Result.success(userVo);
        }
        return Result.failure(ResultCode.RESULT_CODE_DATA_NONE);
    }

    @Override
    public Result getUserById(int id) {
        UserVo user = null;
        try {
            user = userMapper.getUserById(id);
        } catch (SQLException e) {
            logger.error("查询所有用户出现异常");
        }
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.failure(ResultCode.RESULT_CODE_DATA_NONE);
        }
    }

    @Override
    public Result getDynamicById(int id) {
        UserVo userVo = new UserVo();
        try {
            userVo = userMapper.getDynamicById(id);
        } catch (SQLException e) {
            logger.error("用户动态查询异常");
        }
        if(userVo != null){
            return Result.success(userVo);
        }
        return Result.failure(ResultCode.RESULT_CODE_DATA_NONE);
    }

}