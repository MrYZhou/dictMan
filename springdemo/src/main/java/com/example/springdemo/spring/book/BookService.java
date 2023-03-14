package  com.example.springdemo.spring.book;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springdemo.spring.book.model.BookPage;

import java.util.List;

public interface BookService extends IService<BookEntity> {
    List<BookEntity> getList(BookPage page);
}
