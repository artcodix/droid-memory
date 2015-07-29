package com.artcodix.lib.droidmemory;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Class for the database module. This is used to simplify database implementation for
 * any upcoming android app. It holds the basic database information as well as every table used in
 * the current application.
 * 
 * The table creation string must be stored in a member! The name of this member must be in the
 * following form: "TABLE_YOURTABLENAME"!
 * 
 * @author Marco Schweizer
 * @version 0.1
 *
 */

public class DatabaseConfiguration {

	protected String mDatabaseName = "droidmemory.db";
	protected int mDatabaseVersion = 1;
	
	/**
	 * ========================================
	 * 				TABLES
	 * ========================================
	 */

	/**
	 * ========================================
	 * 				METHODS
	 * ========================================
	 */
	
	/**
	 * Retrieves the table queries using java reflection. Query variables therefore need to be named
	 * in a specified way in order to be recognized by this method.
	 * @return a Map containing the table name as key and the creation query as value
	 */
	public Map<String, String> getTables() {
		
		Map<String, String> tables = new HashMap<String, String>();
		
		try {
			Class<?> clz = this.getClass();
			Field[] fields = clz.getDeclaredFields();
			for(Field field : fields) {
				String name = field.getName();
				if(name.startsWith("TABLE_")) {
					String table = (String) field.get(this);
					tables.put(name.replace("TABLE_", "").toLowerCase(), table);
				}
			}
		} catch(IllegalArgumentException e) {
			Log.e(this.getClass().getName(), "Failed to retrieve database tables", e);
		} catch (IllegalAccessException e) {
			Log.e(this.getClass().getName(), "Failed to retrieve database tables", e);
		}
		
		
		return tables;
	}

	protected String getDatabaseName() {
		return mDatabaseName;
	}

	protected void setDatabaseName(String mDatabaseName) {
		this.mDatabaseName = mDatabaseName;
	}

	protected int getDatabaseVersion() {
		return mDatabaseVersion;
	}

	protected void setDatabaseVersion(int mDatabaseVersion) {
		this.mDatabaseVersion = mDatabaseVersion;
	}
}
