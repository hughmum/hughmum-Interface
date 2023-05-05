package com.mu.api.common;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mu.api.mapper.AuthMapper;
import com.mu.api.mapper.UserMapper;
import com.mu.api.model.entity.Auth;
import com.mu.api.model.entity.User;
import common.vo.LoginUserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 沐
 */
@Component
public class Oauth2LoginUtils {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private GenerateAuthUtils generateAuthUtils;

    @Autowired
    private AuthMapper authMapper;

    /**
     * 通过gitee 或者 github 进行登录
     * @param response
     * @return
     */
    public LoginUserVo giteeOrGithubOauth2Login(HttpResponse response){
        JSONObject obj = JSONUtil.parseObj(response.body());
        String userAccount = String.valueOf(obj.get("login"));
        String name = String.valueOf(obj.get("name"));
        String userAvatar = String.valueOf(obj.get("avatar_url"));
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("userAccount", userAccount));
        LoginUserVo loginUserVo = new LoginUserVo();
        if (null != user){
            user.setUserPassword(null);
            String mobile = user.getMobile();
            String newMobile = mobile.substring(0, 3) + "****" + mobile.substring(7);
            user.setMobile(newMobile);
            BeanUtils.copyProperties(user,loginUserVo);
            String token = tokenUtils.generateToken(String.valueOf(loginUserVo.getId()),loginUserVo.getUserAccount());
            loginUserVo.setToken(token);
        }else {
            //AppId 是用于标识一个应用的唯一标识符，是应用在使用 OAuth2 登录时向 Gitee 或 Github 申请的，用于区分不同的应用。在这段代码中，每次生成一个新用户时，都会生成一个随机的 AppId，并将其存储到数据库中，用于后续的签名计算。具体来说，AppId 会被用于生成 AccessKey、SecretKey 和 Token。其中，AccessKey 和 SecretKey 是用于计算签名字符串的，Token 是用于身份验证和授权的，这些信息都是和应用相关的，因此需要使用 AppId 来区分不同的应用。同时，AppId 也可以用于后续的应用管理和统计分析等功能。
            String appId = String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, 9 - 1)));
            String accessKey = generateAuthUtils.accessKey(appId);
            String secretKey = generateAuthUtils.secretKey(appId,userAccount);
            //Token 是用于生成签名字符串的，它是通过 generateAuthUtils.token() 方法生成的，其中包含了 AppId、AccessKey、SecretKey 和用户账号等信息。这个 Token 会被存储到数据库中，用于后续的签名计算。在生成签名字符串时，需要使用这个 Token，以确保签名字符串的正确性和安全性。
            String token = generateAuthUtils.token(appId,userAccount, accessKey, secretKey);
            Auth auth = new Auth();
            auth.setUseraccount(userAccount);
            auth.setAppid(Integer.valueOf(appId));
            auth.setAccesskey(accessKey);
            auth.setSecretkey(secretKey);
            auth.setToken(token);
            User user1 = new User();
            user1.setUserAccount(userAccount);
            user1.setUserName(name);
            user1.setUserAvatar(userAvatar);
            userMapper.insert(user1);
            auth.setUserid(user1.getId());
            authMapper.insert(auth);
            // 进行登录操作
            User user2 = userMapper.selectById(user1.getId());
            BeanUtils.copyProperties(user2,loginUserVo);
            //token1是用于身份验证和授权的，它是通过调用 tokenUtils.generateToken() 方法生成的，其中包含了用户的 ID 和账号信息。这个 Token 会被返回给客户端，客户端在后续的请求中需要携带这个 Token，服务器会根据 Token 中的信息进行身份验证和授权，以确保请求的合法性和安全性。
            String token1 = tokenUtils.generateToken(String.valueOf(loginUserVo.getId()),loginUserVo.getUserAccount());
            loginUserVo.setToken(token1);
        }
        return loginUserVo;
    }
}
