package  com.example.springdemo.spring.book.model;


import com.example.springdemo.spring.book.DictEntity;
import com.larry.trans.DictValue;
import com.larry.trans.RelationTable;
import lombok.Data;

@Data
//@RelationTables({
//        @RelationTable(bindKey = "id",target = DictEntity.class)
//})

@RelationTable(bindKey = "id",target = DictEntity.class)
public class BookInfo {

//    String id;
//    String name;
//
//    String tag;
//    private String did;
//    @DictValue(ref="book")
//    String type;
    @DictValue("name")
    String type;


}
