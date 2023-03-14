package com.larry.base;

import lombok.Data;

import java.util.List;

@Data
public class PageListVO<T> {
    List<T> list;
    com.larry.base.PaginationVO<T> pagination;
}
