package com.hkbae.recommender;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    // id를 통해 User 정보 조회
    @Select("select * from User where id=#{id}")
    User getUser(@Param("id") String id);

    // id를 통해 user가 존재하는지 조회
    @Select("select count(*) from User where id=#{id}")
    int getUserCount(@Param("id") String id);

    // id,pw에 따른 User 가져오기
    @Select("select * from User where id = #{id} and password=#{password} union select '','','','' from User order by id desc limit 1;")
    User login(@Param("id") String id, @Param("password") String password);

    // 사용자 정보 추가 (id,pw,name,born)
    @Insert("insert into User values(#{user.id}, #{user.password}, #{user.name}, #{user.born})")
    int insertUser(@Param("user") User user);

    // 특정 유저 비밀번호 변경
    @Update("update User set password=#{password} where id=#{id}")
    int updateUserPassword(@Param("id") String id, @Param("password") String password);

    // 특정 유저 기본정보 변경
    @Update("update User set name=#{name}, born=#{born} where id=#{id}")
    int updateUserInfo(@Param("id") String id, @Param("name") String name, @Param("born") String born);

    // 사용자 정보 삭제
    @Delete("delete from User where id=#{id}")
    int deleteUser(@Param("id") String id);

}
