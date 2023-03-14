package  com.example.springdemo.spring.book;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springdemo.spring.book.model.BookPage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, BookEntity> implements BookService {

    @Override
    public List<BookEntity> getList(BookPage page) {
        return this.baseMapper.getList(page);
    }
}
