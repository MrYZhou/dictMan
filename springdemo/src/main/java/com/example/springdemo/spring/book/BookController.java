package com.example.springdemo.spring.book;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springdemo.base.AppResult;
import com.example.springdemo.spring.book.model.BookInfo;
import com.example.springdemo.spring.book.model.BookPage;
import com.larry.trans.DictMany;
import com.larry.trans.DictOne;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @DictMany(value = BookInfo.class, key = "data.records")
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
    @DictOne(BookInfo.class)
    public AppResult<Object> info(@PathVariable(value = "id") String id) {
        BookEntity info = bookService.getById(id);
        return AppResult.success(info);
    }


}
