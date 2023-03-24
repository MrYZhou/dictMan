package com.example.springdemo.base;

import lombok.Data;

import java.util.List;

@Data
public class PageListVO<T> {
    List<T> list;
    PaginationVO<T> pagination;
}
