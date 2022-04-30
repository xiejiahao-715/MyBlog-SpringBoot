package com.xjh.myblog;

import com.xjh.myblog.app.task.SyncBlogViewCountTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyBlogApplication.class)
public class SyncBlogViewCountTaskTest {

    @Autowired
    private SyncBlogViewCountTask syncBlogViewCountTask;

    @Test
    public void testSync(){
        syncBlogViewCountTask.sync();
    }
}
