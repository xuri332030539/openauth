package com.example.springboot.dao;

import com.example.springboot.po.LiuJiaPO;
import com.example.springboot.po.StudentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface CeshiDao {
    List<StudentPO> openauth();

    void afterOpenauth(@Param("addTime")String addTime,@Param("id")int id);

    List<LiuJiaPO> sendLiuJia();

    void afterSendLiuJia(@Param("id")int id);
}
