package com.example.android.popularmovies.utilities;

/**
 * Firebase helper class to provide the FirebaseAuth and FirebaseDatabase object throughout the
 * scope of the application so that every time authentication or database operation is required in
 * the application Auth and Database access objects aren't required to be declared and instantiated
 * again and again
 * The init method will be invoked right at the opening of the application
 */

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseDatabase firebaseDatabase;

    public static void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public static FirebaseAuth getFirebaseAuthObject(){
        return firebaseAuth;
    }

    public static FirebaseDatabase getFirebaseDatabaseObject(){
        return firebaseDatabase;
    }
}
