package  com.example.springdemo.spring.book.model;


import com.example.springdemo.spring.book.DictEntity;
import com.larry.trans.DictValue;
import com.larry.trans.RelationTable;
import com.larry.trans.RelationTables;
import lombok.Data;

@Data
@RelationTables({
        @RelationTable(bindKey = "id",target = DictEntity.class)
})
public class BookInfo {

    String id;
    String name;

    String tag;
    @DictValue("name")
    String type;

    //SIMPLE 翻译，用于关联其他的表进行翻译    schoolName 为 School 的一个字段
//    @Trans(type = TransType.SIMPLE, target = Place.class, fields = "name")
    private String did;
}
