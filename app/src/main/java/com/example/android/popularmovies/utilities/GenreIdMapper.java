package com.example.android.popularmovies.utilities;

import java.util.HashMap;

public class GenreIdMapper {

    public static String mapIdToGenre(int id){
        HashMap<Integer, String> map = new HashMap<>();
        map.put(28, "Action");
        map.put(12, "Adventure");
        map.put(16, "Animation");
        map.put(35, "Comedy");
        map.put(80, "Crime");
        map.put(99, "Documentary");
        map.put(18, "Drama");
        map.put(10751, "Family");
        map.put(14, "Fantasy");
        map.put(36, "History");
        map.put(27, "Horror");
        map.put(10402, "Music");
        map.put(9648, "Mystery");
        map.put(10749, "Romance");
        map.put(878, "Science Fiction");
        map.put(10770, "TV Movie");
        map.put(53, "Thriller");
        map.put(10752, "War");
        map.put(37, "Western");
        return map.get(id);
    }
}
