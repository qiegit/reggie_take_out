package com.atqie.reggie.controller;

import com.atqie.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @Author 郄
 * @Date 2022/6/22 16:03
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String filePath;

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws Exception {
//        输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(filePath+name);
        int len = 0;
        byte[] bytes = new byte[1024];

//        输出流，通过输出流将文件写会浏览器，在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");

        while ((len = fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        fileInputStream.close();
        outputStream.close();

    }
    @PostMapping("upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info("========================file:{},filename:{}",file);

        String filename = file.getOriginalFilename();
//        处理文件重名
        String suffix = filename.substring(filename.lastIndexOf("."));
        filename = UUID.randomUUID().toString() + suffix;

        File file1 = new File(filePath);
        if (!file1.exists()){
            file1.mkdirs();
        }
        String filePath1 = filePath+filename;
        file.transferTo(new File(filePath1));

        return R.success(filename);
    }
}
