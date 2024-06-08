package com.example.kvizuma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String NAME = "name";
    public static final String SCORE = "score";
    public static final String HIGHSCORES_MATH = "highscores_math";
    private static final String TAG = "SQLiteOpenHelper";
    private final Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "KvizumaDB.db";
    private boolean createDb = false, upgradeDb = false;
    public enum Operations {
        ADDITION,
        DIVISION,
        MULTIPLICATION,
        SUBTRACTION,
        FINAL_MATH,
        FLAGS,
        BORDERS,
        CAPITOLS,
        FINAL_GEO
    }
    public enum Disciplines{
        MATH,
        GEOGRAPHY
    }

    public DBHelper(@Nullable Context context) {
        super(context, "KvizumaDB.db", null, 1);
        this.context = context;

    }


    private void copyDatabaseFromAssets(SQLiteDatabase db) {
        Log.i(TAG, "copyDatabase");
        InputStream myInput = null;
        OutputStream myOutput = null;
        db.disableWriteAheadLogging();
        try {
            myInput = context.getAssets().open("databases/KvizumaDB.db");

            myOutput = new FileOutputStream(db.getPath());

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();

            SQLiteDatabase copiedDb = context.openOrCreateDatabase(
                    DATABASE_NAME, 0, null);
            copiedDb.execSQL("PRAGMA user_version = " + DATABASE_VERSION);
            copiedDb.close();


        } catch (IOException e) {
            e.printStackTrace();
            throw new Error(TAG + " Error copying database");
        } finally {
            try {
                if (myOutput != null) {
                    myOutput.close();
                }
                if (myInput != null) {
                    myInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error(TAG + " Error closing streams");
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade db");
        upgradeDb = true;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG, "onOpen db");
        if (createDb) {
            createDb = false;
            copyDatabaseFromAssets(db);
            File wal = new File("/data/data/com.example.kvizuma/databases/KvizumaDB.db-wal");
            if(wal.exists()) wal.delete();
            wal = new File("/data/data/com.example.kvizuma/databases/KvizumaDB.db-shm");
            if(wal.exists()) wal.delete();

        }
        if (upgradeDb) {
            upgradeDb = false;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate db");
        if(!doesTheTableExist(db)) createDb = true;
    }

    public boolean addNewRecord(RecordsSet recordsSet, Disciplines disciplines) {
        String table = switchDisciplines(HIGHSCORES_MATH, "highscores_geography", disciplines);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NAME, recordsSet.getName());
        cv.put(SCORE, recordsSet.getScore());

        long insert = db.insert(table, null, cv);
        db.close();
        if (insert == -1) return false;
        else return true;
    }

    public List<RecordsSet> getEveryoneFromScores(Disciplines disciplines){
        String table = switchDisciplines(HIGHSCORES_MATH, "highscores_geography", disciplines);
        List<RecordsSet> returnList = new ArrayList<>();
        int ranking = 1;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + table + " ORDER BY " + SCORE + " DESC", null);

        if(c.moveToFirst()){
            do{
                String recordName = c.getString(1);
                int recordScore = c.getInt(2);

                RecordsSet recordsSet = new RecordsSet(String.valueOf(ranking), recordName, recordScore);
                returnList.add(recordsSet);
                ranking++;
            } while (c.moveToNext());
        } else{}

        c.close();
        db.close();
        return returnList;
    }

    // Compares new score with score from db. If the new score is larger then returns true.
    public boolean compareScores(int newScore, Operations operation, Disciplines disciplines){
        int id = stageToIDConverter(operation);
        int scoreFromDB;
        String query = switchDisciplines("SELECT high_score_stage FROM stages_math WHERE id=" +id,
                "SELECT stage_high_score FROM stages_geography WHERE id=" +id, disciplines);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        scoreFromDB = c.getInt(0);
        db.close();

        if(newScore > scoreFromDB) return true;
        else return false;
    }

    public void setNewHighScoreInStage(Operations operation, int newScore, String newName, Disciplines disciplines){
        int id = stageToIDConverter(operation);
        String query = switchDisciplines("UPDATE stages_math SET high_score_stage = " + newScore + ", high_score_stage_name = \"" + newName + "\" WHERE id=" + id,
                "UPDATE stages_geography SET stage_high_score = " + newScore + ", stage_high_score_name = \"" + newName + "\" WHERE id=" + id, disciplines);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    //Adds new current provided score if larger and returns if true if it is.
    public int addNewCurrentScore (int newScore, Operations operation, Disciplines disciplines){
        int id = stageToIDConverter(operation);
        int scoreFromDB;
        String selectQuery = switchDisciplines("SELECT current_score FROM stages_math WHERE id=" +id,
                "SELECT stage_current_score FROM stages_geography WHERE id=" +id, disciplines);
        String updateQuery = switchDisciplines("UPDATE stages_math SET current_score=" + newScore + " WHERE id=" + id,
                "UPDATE stages_geography SET stage_current_score=" + newScore + " WHERE id=" + id, disciplines);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        scoreFromDB = c.getInt(0);
        db.close();
        SQLiteDatabase db2 = this.getWritableDatabase();

        if(newScore > scoreFromDB) {
            db2.execSQL(updateQuery);
            db2.close();
            return newScore - scoreFromDB;
        }else {
            db2.close();
            return 0;
        }
    }

    public void resetCurrentScore (Disciplines disciplines){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = switchDisciplines("UPDATE stages_math SET current_score=", "UPDATE stages_geography SET stage_current_score=", disciplines);
        db.execSQL(query + 0);
        db.close();
    }

    public List<RecordsSet> getEveryoneFromStage(Disciplines disciplines){
        List<RecordsSet> returnList = new ArrayList<>();
        int operationCase = 1;
        String operation = "";
        String query = switchDisciplines("SELECT high_score_stage_name, high_score_stage FROM stages_math",
                "SELECT stage_high_score_name, stage_high_score FROM stages_geography", disciplines);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query , null);

        if(c.moveToFirst()){
            do{
                String recordName = c.getString(0);
                int recordScore = c.getInt(1);
                if(disciplines == Disciplines.MATH) {
                    switch (operationCase) {
                        case 1:
                            operation = "Sabiranje-";
                            break;
                        case 2:
                            operation = "Oduzimanje-";
                            break;
                        case 3:
                            operation = "Množenje-";
                            break;
                        case 4:
                            operation = "Deljenje-";
                            break;
                        case 5:
                            operation = "Finale-";
                            break;
                    }
                } else {
                    switch (operationCase) {
                        case 1:
                            operation = "Zastave-";
                            break;
                        case 2:
                            operation = "Granice-";
                            break;
                        case 3:
                            operation = "Glavni gradovi-";
                            break;
                        case 4:
                            operation = "Finale-";
                            break;
                    }
                }

                RecordsSet recordsSet = new RecordsSet(operation, recordName, recordScore);
                operationCase++;
                returnList.add(recordsSet);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return returnList;
    }

    public int getTotalScoreFromDB(Disciplines disciplines){
        int sc;
        String query = switchDisciplines("SELECT total_score FROM total_score WHERE id=1",
                "SELECT total_score FROM total_score WHERE id=2", disciplines);
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i("AA", db.getPath());

        Cursor c = db.rawQuery(query, null);

            c.moveToFirst();
            sc = c.getInt(0);

            c.close();
            db.close();
            return sc;
    }

    public void setNewTotalScoreAtDB(int newScore, Disciplines disciplines){
        String query = switchDisciplines("UPDATE total_score SET total_score=" + newScore + " WHERE id=1",
        "UPDATE total_score SET total_score=" + newScore + " WHERE id=2", disciplines);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public boolean getStageAvailabilityInDB (Operations operation, Disciplines disciplines){
        int id= stageToIDConverter(operation);
        String query = switchDisciplines("SELECT stage_state FROM stages_math WHERE id=",
                "SELECT stage_state FROM stages_geography WHERE id=", disciplines);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(query + id, null);

        c.moveToFirst();
        boolean availability = c.getInt(0) == 0 ? false : true;

        c.close();
        db.close();
        return availability;
    }

    public void setStageAvailabilityInDB(Operations operation, boolean available, Disciplines disciplines){
        int id = stageToIDConverter(operation);
        SQLiteDatabase db = this.getWritableDatabase();
        int ava = available == true ? 1 : 0;
        String query = switchDisciplines("UPDATE stages_math SET stage_state=" + ava + " WHERE id=" + id,
                "UPDATE stages_geography SET stage_state=" + ava + " WHERE id=" + id, disciplines);

        db.execSQL(query);
        db.close();
    }
    public CountrySet getCountryByID(int id){
        CountrySet cs = new CountrySet();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM geography_countries WHERE id=" + id, null);

        c.moveToFirst();

        cs.setId(c.getInt(0));
        cs.setCountryName(c.getString(1));
        cs.setCountryCapitol(c.getString(2));
        cs.setCountryBorderImg(c.getBlob(3));
        cs.setCountryFlagImg(c.getBlob(4));
        cs.setCountryBorderCapitolImg(c.getBlob(5));
        cs.setCountryContinent(c.getInt(6));

        c.close();
        db.close();

        return cs;
    }

    public ArrayList<Integer> getAvailableCountriesID(ArrayList<Integer> continentID){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for(int i = 0; i < continentID.size(); i++) {
            int j = 0;
            Cursor c = db.rawQuery("SELECT id FROM geography_countries WHERE continentID=" + continentID.get(i), null);

            if (c.moveToFirst()) {
                do {
                    ret.add(j, c.getInt(0));
                    j++;
                }
                while (c.moveToNext());
            }
            c.close();
        }
        db.close();
        return  ret;
    }

    private int stageToIDConverter(Operations operation){
        int id=1;
        switch(operation){
            case ADDITION:
            case FLAGS:
                id=1;
                break;
            case SUBTRACTION:
            case BORDERS:
                id=2;
                break;
            case MULTIPLICATION:
            case CAPITOLS:
                id=3;
                break;
            case DIVISION:
            case FINAL_GEO:
                id=4;
                break;
            case FINAL_MATH:
                id=5;
                break;
        }
        return id;
    }
    private boolean doesTheTableExist(SQLiteDatabase db){
        try (Cursor c = db.rawQuery("SELECT DISTINCT id FROM stages_math", null)){
            if(c!=null) {
                if(c.getCount()>0) {
                    return true;
                }
            }
            return false;
        }catch (Exception ignore) {
            return false;
        }
    }

    private String switchDisciplines(String stringMath, String stringGeo, Disciplines disciplines){
        switch (disciplines){
            case MATH:
                return stringMath;
            case GEOGRAPHY:
                return stringGeo;
            default:
                throw new IllegalStateException("Unexpected value: " + disciplines);
        }
    }


}
