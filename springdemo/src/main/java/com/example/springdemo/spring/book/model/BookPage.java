package  com.example.springdemo.spring.book.model;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springdemo.spring.book.BookEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BookPage extends Page<BookEntity> {

    private String name;
}
