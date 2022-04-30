package com.xjh.myblog.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.app.entity.Social;
import com.xjh.myblog.app.mapper.SocialMapper;
import com.xjh.myblog.app.service.SocialService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocialServiceImpl extends ServiceImpl<SocialMapper, Social> implements SocialService {
    @Override
    @Cacheable(value = "socialsInfo",key = "'all'")
    @Transactional(readOnly = true)
    public List<Social> getSocials() {
        return this.list();
    }
}
