package com.xjh.myblog;

import com.xjh.myblog.app.cacheservice.AdminCacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyBlogApplication.class)
public class CacheServiceTest {

    @Autowired
    private AdminCacheService adminCacheService;

    @Test
    public void test(){
        adminCacheService.delToken("1");
    }
}
