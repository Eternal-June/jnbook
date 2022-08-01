package com.jn.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jn.core.storage.AliyunStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AliyunStorageTest {
    @Autowired
    private AliyunStorage aliyunStorage;

    @Test
    public void test() throws IOException {
        String test = getClass().getClassLoader().getResource("Jn.png").getFile();
        File testFile = new File(test);
        aliyunStorage.store(new FileInputStream(test), testFile.length(), "image/png", "Jn.png");
        Resource resource = aliyunStorage.loadAsResource("Jn.png");
        String url = aliyunStorage.generateUrl("Jn.png");
        System.out.println("test file " + test);
        System.out.println("store file " + resource.getURI());
        System.out.println("generate url " + url);

//        tencentOsService.delete("Jn.png");
    }

}