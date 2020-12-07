
package com.example.android.popularmovies.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import static com.example.android.popularmovies.utilities.Constant.DATABASE_NAME;

/**
 * {@link MovieDatabase} database for the application including a table for {@link MovieEntry}
 * with the DAO {@link MovieDao}
 */

// List of the entry classes and associated TypeConverters
@Database(entities = {MovieEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String TAG = MovieDatabase.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieDatabase sInstance;

    public static MovieDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Getting the database instance");
        return sInstance;
    }

    // The associated DAOs for the database
    public abstract MovieDao movieDao();
}
