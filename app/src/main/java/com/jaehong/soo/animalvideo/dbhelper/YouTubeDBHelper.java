package com.jaehong.soo.animalvideo.dbhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.jaehong.soo.animalvideo.vo.FavoriteVO;

import java.util.ArrayList;

public class YouTubeDBHelper extends SQLiteOpenHelper{
    private SQLiteDatabase db = null;
    private SQLiteStatement statement = null;
    private ArrayList<FavoriteVO> list;
    private FavoriteVO favoriteVO;


    public YouTubeDBHelper(Context context) {
        super(context, "youTubeDB.db", null , 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS diary;");

        db.execSQL("CREATE TABLE Favorite(video_id TEXT PRIMARY KEY NOT NULL,video_thumbnails NOT NULL, video_title TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS diary;");
    }

    public void favoriteInsert(String videoId,String thumbnails, String title){

        db = getWritableDatabase();

        statement = db.compileStatement("INSERT INTO Favorite(video_id, video_thumbnails, video_title) VALUES(?,?,?)");

        statement.bindString(1,videoId);
        statement.bindString(2,thumbnails);
        statement.bindString(3,title);

        statement.execute();

        statement.close();
        db.close();
    }

    public void favoriteClear(String videoId){
        db = getWritableDatabase();

        statement = db.compileStatement("DELETE FROM Favorite WHERE video_id = ?");
        statement.bindString(1,videoId);
        statement.execute();

        statement.close();
        db.close();
    }

    public void favoriteClear(){
        db = getWritableDatabase();

        statement = db.compileStatement("DELETE FROM Favorite");
        statement.execute();

        statement.close();
        db.close();
    }

    public boolean favoriteDataCheck(String videoId){
        StringBuffer sql = new StringBuffer();
        db = getReadableDatabase();

        sql.append("SELECT video_id FROM Favorite WHERE video_id = '");
        sql.append(videoId);
        sql.append("';");

        Log.e("sql", sql.toString());

        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToNext())
            return false;
        else
            return true;
    }

    public ArrayList<FavoriteVO> favoriteDataAllSelect(){
        list = new ArrayList<>();

        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Favorite", null);

        while (cursor.moveToNext()){
            favoriteVO = new FavoriteVO();
            favoriteVO.setVideoId(cursor.getString(0));
            favoriteVO.setVideoThumbnails(cursor.getString(1));
            favoriteVO.setVideoTitle(cursor.getString(2));
            list.add(favoriteVO);
        }

        db.close();

        return list;
    }

}
