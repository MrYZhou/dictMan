# dictMan
java字典翻译

先用init目录下的sql初始化下库。然后看springdemo的示例


## 1.添加maven

```
<dependency>
    <groupId>io.github.mryzhou</groupId>
    <artifactId>dict-man-spring-boot-starter</artifactId>
    <version>1.2.0</version>
</dependency>
```

## 2.初始化

```
public class SpringdemoApplication implements ApplicationListener<ApplicationStartedEvent> {
    //    // 初始化字典的数据
    @Autowired
    DictService dictService;

    public static void main(String[] args) {
        SpringApplication.run(SpringdemoApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Map<String, String> transMap = new HashMap<>();
        transMap.put("0", "书籍1");
        transMap.put("1", "书籍2");
        // 带类别的
        dictService.putDictType("book", transMap);
        // 不带类别
        dictService.putDictItem("355643017543027584","书籍3");

        // 配置数据源
        Map<String, DbContext> db = dictService.getDb();
        String url = "jdbc:mysql://127.0.0.1:3306/study?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=" +
                "UTC&allowPublicKeyRetrieval=true";
        String username = "root";
        String password ="root";
        DbContext context  = new DbContext("",url,username,password);
        db.put("main",context);
        dictService.setDb(db);

    }

}
```

## 3.使用

新建一个类作为vo类,在上面加注解

```
@Data
// @RelationTables({
// @RelationTable(primaryKey = "id",target = DictEntity.class)
// })

@RelationTable(primaryKey = "id", target = DictEntity.class)
public class BookInfo {

    private String id;
    private String name;
    private String tag;

    // 1. 普通字典翻译
    @DictValue(ref = "book")
    private String type;

    // 2. 关联表id的翻译建议通过newKey加个别名
    @DictValue(value = "name", newKey = "bookName2")
    private String dictId;
}
```

在controller接口那边加一个注解,绑定这个vo类

```
 /**
     * 获取列表
     *
     * @param page
     * @return
     */
    @PostMapping("/list")
    @DictMany(value = BookInfo.class, key = "data.records")
    @DictMany(value = BookInfo.class) // 默认认为数据存在res.data.list.
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
```

## 5.可选配置

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/71ce658215584c3f9e15d5494d71f377~tplv-k3u1fbpfcp-watermark.image?)
```
dictman:
  primaryKey: id    #默认表主键是解析id
  resultList: data.records #默认data.list


