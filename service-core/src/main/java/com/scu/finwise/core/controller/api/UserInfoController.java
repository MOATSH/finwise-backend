package com.scu.finwise.core.controller.api;


import com.scu.finwise.base.config.util.JwtUtils;
import com.scu.finwise.common.exception.Assert;
import com.scu.finwise.common.result.R;
import com.scu.finwise.common.result.ResponseEnum;
import com.scu.finwise.common.util.RegexValidateUtils;
import com.scu.finwise.core.pojo.vo.LoginVO;
import com.scu.finwise.core.pojo.vo.RegisterVO;
import com.scu.finwise.core.pojo.vo.UserInfoVO;
import com.scu.finwise.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author MOATSH
 * @since 2023-10-26
 */
@Api(tags = "会员接口")
@RestController
@RequestMapping("/api/core/userInfo")
@Slf4j
@CrossOrigin
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private MyFeignClient myFeignClient;


    @ApiOperation("会员注册")
    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO){

        String mobile = registerVO.getMobile();
        String password = registerVO.getPassword();
        String code = registerVO.getCode();

        //MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        //PASSWORD_NULL_ERROR(-204, "密码不能为空"),
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        //CODE_NULL_ERROR(-205, "验证码不能为空"),
        Assert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);

        //校验验证码
        String codeGen = (String)redisTemplate.opsForValue().get("finwise:sms:code:" + mobile);
        //CODE_ERROR(-206, "验证码不正确"),
        Assert.equals(code, codeGen, ResponseEnum.CODE_ERROR);

        //注册
        userInfoService.register(registerVO);
        return R.ok().changeMessage("注册成功");
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request) {

        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        String ip = request.getRemoteAddr();
        UserInfoVO userInfoVO = userInfoService.login(loginVO, ip);

        return R.ok().data("userInfo", userInfoVO);
    }
    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {

        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);

        if(result){
            return R.ok();
        }else{
            //LOGIN_AUTH_ERROR(-211, "未登录"),
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }

    @GetMapping("/testFeignClient")
    public void testFeignClient() {
        String someData = myFeignClient.getSomeData();
        System.out.println(someData);
    }
}

