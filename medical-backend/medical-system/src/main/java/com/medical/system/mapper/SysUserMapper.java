package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Mapper
 *
 * @author 刘柏城
 * @date 2025-11-06
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}

