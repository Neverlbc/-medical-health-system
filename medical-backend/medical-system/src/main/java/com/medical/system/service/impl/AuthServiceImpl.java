package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.common.exception.BusinessException;
import com.medical.common.result.ResultCode;
import com.medical.common.utils.JwtUtils;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.dto.LoginDTO;
import com.medical.system.dto.RegisterDTO;
import com.medical.system.entity.PatientInfo;
import com.medical.system.entity.SysUser;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.mapper.DoctorInfoMapper;
import com.medical.system.mapper.SysUserMapper;
import com.medical.system.entity.DoctorInfo;
import com.medical.system.service.AuthService;
import com.medical.system.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Auth service implementation.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        // validate role
        if (!isValidRole(dto.getRole())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "invalid role");
        }

        // unique username
        LambdaQueryWrapper<SysUser> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(SysUser::getUsername, dto.getUsername());
        if (sysUserMapper.selectCount(usernameQuery) > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // unique phone
        LambdaQueryWrapper<SysUser> phoneQuery = new LambdaQueryWrapper<>();
        phoneQuery.eq(SysUser::getPhone, dto.getPhone());
        if (sysUserMapper.selectCount(phoneQuery) > 0) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        // unique email if provided
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            LambdaQueryWrapper<SysUser> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(SysUser::getEmail, dto.getEmail());
            if (sysUserMapper.selectCount(emailQuery) > 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "email already used");
            }
        }

        // create user
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setStatus(1);
        user.setNickname(dto.getUsername());

        sysUserMapper.insert(user);

        // create patient info if role is PATIENT
        if ("PATIENT".equals(dto.getRole())) {
            PatientInfo patientInfo = new PatientInfo();
            patientInfo.setUserId(user.getId());
            patientInfo.setRealName(user.getNickname());
            // DB schema requires non-null gender; use 0 as default placeholder (0: male, 1: female).
            // When profile completion is implemented, this should be updated by the user.
            patientInfo.setGender(0);
            patientInfoMapper.insert(patientInfo);
        } else if ("DOCTOR".equals(dto.getRole())) {
            DoctorInfo doctorInfo = new DoctorInfo();
            doctorInfo.setUserId(user.getId());
            doctorInfo.setRealName(user.getNickname());
            doctorInfo.setGender(0);
            doctorInfo.setDepartment("全科"); // 默认科室
            doctorInfo.setStatus(1); // 默认正常
            doctorInfoMapper.insert(doctorInfo);
        }

        log.info("register ok: username={}, role={}", dto.getUsername(), dto.getRole());
    }

    private boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "DOCTOR".equals(role) || "PATIENT".equals(role);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // query user by username
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("login fail: username not found - {}", dto.getUsername());
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // verify password
        if (!SecurityUtils.matchesPassword(dto.getPassword(), user.getPassword())) {
            log.warn("login fail: bad password - {}", dto.getUsername());
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // check status
        if (user.getStatus() == 0) {
            log.warn("login fail: disabled - {}", dto.getUsername());
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // update last login time
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        // issue token (fixed expiration from config)
        Long tokenExpiration = jwtExpiration;
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        // build response
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        vo.setToken(token);
        vo.setExpiresIn(tokenExpiration);

        log.info("login ok: username={}, role={}", dto.getUsername(), user.getRole());
        return vo;
    }

    @Override
    public void logout() {
        // stateless JWT; optionally add to blacklist (e.g., Redis). No-op here.
        log.info("logout ok");
    }
}
