package com.mu.order.common;

import com.github.wujun234.uid.impl.CachedUidGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 沐
 * Date: 2023-04-23 12:05
 * version: 1.0
 */
@Component
public class IdGenerator {
    @Resource
    private CachedUidGenerator cachedUidGenerator;

    /**
     * 获取uid
     *
     * @return
     */
    public long getUId() {
        return cachedUidGenerator.getUID();
    }

    /**
     * 直接获取获取uid的string类型
     *
     * @return
     */
    public String getStringUId() {
        return String.valueOf(cachedUidGenerator.getUID());
    }

    /**
     * 格式化传入的uid，方便查看其实际含义
     *
     * @param uid
     * @return
     */
    public String parse(long uid) {
        return cachedUidGenerator.parseUID(uid);
    }

}
