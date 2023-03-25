package com.example.springdemo.spring.book;


import java.io.IOException;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springdemo.base.AppResult;
import com.example.springdemo.spring.book.model.BookInfo;
import com.example.springdemo.spring.book.model.BookPage;
import com.larry.trans.DictMany;
import com.larry.trans.DictOne;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
public class BookController {
        
    private final BookService bookService;

    BookController(BookService bookService
    ) {
        this.bookService = bookService;
    }

    /**
     * 获取列表
     *
     * @param page
     * @return
     */
    @PostMapping("/list")
    // @DictMany(value = BookInfo.class, key = "data.records")
    public AppResult<Object> list(@RequestBody @Validated BookPage page) throws NoSuchMethodException {
        QueryWrapper<BookEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BookEntity::getName, page.getName());
        
        BookPage info = bookService.page(page, wrapper);
        return AppResult.success(info);
    }

    /**
     * 查询信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    // @DictOne(BookInfo.class)
    public AppResult<Object> info(@PathVariable(value = "id") String id) {
        BookEntity info = bookService.getById(id);
        return AppResult.success(info);
    }

    /**
     * 生成测试接口
     *
     * @return
     */
    @GetMapping("/test")
    public AppResult<Object> test() {
         // 发送HTTP请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8081/book/1560526548464287746");
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String result = null;
        try {
            response = httpClient.execute(httpGet);
            entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return AppResult.success(result);
    }

    

   
@GetMapping("/testBatchPost")
public AppResult<Object> testBatchPost() {
    BookPage page;
    page = new BookPage();
    page.setName("Java");
    page.setCurrent(1);
    page.setSize(10);

        
     // 发送HTTP请求
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost("http://localhost:8081/book/list");
    httpPost.setHeader("Content-Type", "application/json");
    
    StringEntity entity = new StringEntity("{\"current\":\"0\",\"size\":10,\"name\":\"java\"}", "UTF-8");
    

    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;
    HttpEntity httpEntity = null;
    String result = null;
    try {
        response = httpClient.execute(httpPost);
        httpEntity = response.getEntity();
        result = EntityUtils.toString(httpEntity, "UTF-8");
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    return AppResult.success(result);
}

    
}
