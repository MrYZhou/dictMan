package  com.example.springdemo.spring.book;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springdemo.spring.book.model.BookPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface BookMapper extends BaseMapper<BookEntity> {
    List<BookEntity> getList(@Param("page") BookPage page);
}
