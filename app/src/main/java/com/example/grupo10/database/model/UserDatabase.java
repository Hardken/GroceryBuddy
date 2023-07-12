package com.example.grupo10.database.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.grupo10.database.model.dao.Userdao;
import com.example.grupo10.database.model.model.User;
import com.example.grupo10.util.Constant;
@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract Userdao getUserdao();
    private static UserDatabase userDB;

    public static UserDatabase getInstance (Context context){
        if (userDB == null){
            userDB = buildUserDatabaseBuilder(context);
        }

        return userDB;
    }

    private static UserDatabase buildUserDatabaseBuilder(Context context){
        return Room.databaseBuilder(context, UserDatabase.class, Constant.NAME_DATABASE)
                .allowMainThreadQueries().build();
    }

}
