package com.mu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.order.mapper.ApiOrderLockMapper;
import com.mu.order.model.entity.ApiOrderLock;
import com.mu.order.service.ApiOrderLockService;
import org.springframework.stereotype.Service;

/**
* @author 12866
* @description 针对表【order_lock】的数据库操作Service实现
* @createDate 2023-03-16 11:17:54
*/
@Service
public class ApiOrderLockServiceImpl extends ServiceImpl<ApiOrderLockMapper, ApiOrderLock>
    implements ApiOrderLockService {

    /**
     * 扣减库存
     * @param orderSn
     */
    @Override
    public void orderPaySuccess(String orderSn) {
        this.update(new UpdateWrapper<ApiOrderLock>().eq("orderSn",orderSn).set("lockStatus",2));
    }
}




