package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.RefreshToken;

import java.time.LocalDateTime;

/**
 * 刷新令牌Mapper接口
 */
@Mapper
public interface RefreshTokenMapper {

    /**
     * 插入刷新令牌
     */
    int insert(RefreshToken refreshToken);

    /**
     * 根据 token 查询刷新令牌
     */
    RefreshToken selectByToken(@Param("token") String token);

    /**
     * 吊销刷新令牌
     */
    int revokeByToken(@Param("token") String token);

    /**
     * 根据用户ID删除所有刷新令牌
     */
    int deleteByEmpId(@Param("empId") Integer empId);
}
