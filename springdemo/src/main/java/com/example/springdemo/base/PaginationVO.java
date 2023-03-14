package com.larry.base;

import lombok.Data;

import java.util.List;

@Data
public class PaginationVO<T>{
    List<T> list;
    T pagination;
}
