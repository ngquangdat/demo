package com.example.demo.factory.response;

import com.example.demo.util.TextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class GeneralResponse<T> {
    private Status status;
    private T data;

    @Override
    public String toString() {
        return TextUtil.gson.toJson(this);
    }
}
