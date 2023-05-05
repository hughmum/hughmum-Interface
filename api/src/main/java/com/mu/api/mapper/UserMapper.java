package com.mu.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.api.model.entity.User;
import common.vo.EchartsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Entity com.mu.api.model.domain.User
 */
public interface UserMapper extends BaseMapper<User> {

    String getMobile(@Param("username") String username);

    String selectPhone(@Param("mobile") String mobile);

    List<EchartsVo> getUserList(@Param("dateList") List<String> dateList);
}




