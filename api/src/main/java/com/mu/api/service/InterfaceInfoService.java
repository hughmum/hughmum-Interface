package com.mu.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mu.api.common.DeleteRequest;
import com.mu.api.common.IdRequest;
import com.mu.api.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.mu.api.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.mu.api.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.mu.api.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mu.api.model.vo.AllInterfaceInfoVo;
import com.mu.api.model.vo.InterfaceInfoVo;
import common.BaseResponse;

import javax.servlet.http.HttpServletRequest;

/**
* @author 沐
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-01-12 10:45:11
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 分页获取已所有的列表（包括已下线）
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    BaseResponse<Page<AllInterfaceInfoVo>> getAllInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request);

    /**
     * 更新操作
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    BaseResponse<Boolean> updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request);

    /**
     * 添加接口
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    BaseResponse<Long> addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request);

    /**
     * 删除操作
     * @param deleteRequest
     * @param request
     * @return
     */
    BaseResponse<Boolean> deleteInterfaceInfo(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 分页获取已开启的列表
     * @param interfaceInfoQueryRequest
     * @return
     */
    BaseResponse<Page<AllInterfaceInfoVo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 上线接口
     * @param idRequest
     * @return
     */
    BaseResponse<Boolean> onlineInterfaceInfo(IdRequest idRequest);

    /**
     * 下线接口
     * @param idRequest
     * @return
     */
    BaseResponse<Boolean> offlineInterfaceInfo(IdRequest idRequest);

    /**
     * 根据 id 获取接口详细信息和用户调用次数
     * @param id
     * @param request
     * @return
     */
    BaseResponse<InterfaceInfoVo> getInterfaceInfoById(long id, HttpServletRequest request);
}
