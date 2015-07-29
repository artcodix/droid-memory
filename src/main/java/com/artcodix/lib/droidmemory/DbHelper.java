package com.artcodix.lib.droidmemory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Map;

/**
 * This class handles database creation and upgrade. It uses the database configuration to
 * create the database with its specified tables and columns.
 * 
 * @author Marco Schweizer
 * @version 0.2
 * @since 29.07.2015
 *
 */
public class DbHelper extends SQLiteOpenHelper {	

	private DatabaseConfiguration config;

	public DbHelper(Context context, DatabaseConfiguration configuration) {
		super(context, configuration.getDatabaseName(), null, configuration.getDatabaseVersion());
		config = configuration;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DatabaseConfiguration config = new DatabaseConfiguration();
		Map<String, String> tables = config.getTables();
		for(Map.Entry<String, String> entry : tables.entrySet()) {
			db.execSQL(entry.getValue());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(getClass().getName(), "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		
		Map<String, String> tables = config.getTables();
		for(Map.Entry<String, String> entry : tables.entrySet()) {
			db.execSQL("DROP TABLE IF EXISTS " + entry.getKey());
		}
	    onCreate(db);
	}

}
