package com.example.grupo10.database.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.grupo10.database.model.model.User;
import com.example.grupo10.util.Constant;

import java.util.List;
@Dao
public interface Userdao {
    @Insert
    long insertUser(User user);
    @Update
    int updateUser(User user);
    @Delete
    void deleteUser(User user);

    @Query("SELECT * from " + Constant.NAME_TABLE_USER)
    List<User> getUser();

    @Query("SELECT * FROM " + Constant.NAME_TABLE_USER + " WHERE email = :email and password = :password")
    User getUserLogin(String email, String password);
}
