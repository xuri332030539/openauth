package com.example.springboot.po;

import lombok.Data;

import java.util.Date;

@Data
public class StudentPO {
    private int id;
    private String name;
    private String personCode;
    private Date  startTime;
    private Date  endTime;
}
