package com.example.mymovies.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {MoviesEntity.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract MoviesDAO moviesDAO();

    private static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                            Database.class, "NewDatabase")
                    .addCallback(sRoomDatabaseCallback)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new androidx.room.RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }
            };
}
