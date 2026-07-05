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
     * 清理过期或已吊销的刷新令牌
     * @return 删除的记录数
     */
    int deleteExpired();
}
