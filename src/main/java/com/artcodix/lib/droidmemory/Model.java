package com.artcodix.lib.droidmemory;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.artcodix.lib.droidmemory.util.DateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The abstract Model base class. It provides basic functionality needed throughout
 * every Model and makes sure that every necessary operation is declared. Abstract methods
 * must be implemented in subclasses.
 * 
 * Every subclass needs to call the constructor with the table name used, so it can be
 * retrieved via the getTableName() method.
 * 
 * @author Marco Schweizer
 * @version 0.1
 * @since 29.04.2014
 *
 */
public abstract class Model implements Comparable<Model> {
	
	/**
	 * ===============================
	 * CONSTANTS
	 * ===============================
	 */

	public final static String COLUMN_ID = "_id";
	public final static String COLUMN_DELETED = "deleted";
	public final static String COLUMN_MODIFIED = "modified";
	public final static String COLUMN_DATE = "date";
	
	/**
	 * ===============================
	 * MEMBERS
	 * ===============================
	 */
	
	private String mTableName;
	private static DataSource mDataSource;
	protected Context mContext;
	
	protected long id;
	protected int deleted;
	protected String modified;
	protected String date;
	
	/**
	 * ===============================
	 * CONSTRUCTORS
	 * ===============================
	 */
	
	/**
	 * Basic constructor.
	 * @param tableName The name of the table this model refers to.
	 */
	public Model(String tableName, Context context) {
		mTableName = tableName;
		mContext = context;
		mDataSource = getDataSource();
		mDataSource.init(mContext, getDatabaseConfiguration());
		
		this.id = 0;
		this.deleted = 0;
		this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mContext.getResources().getConfiguration().locale).format(new Date());
		this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mContext.getResources().getConfiguration().locale).format(new Date());
	}
	
	/**
	 * ===============================
	 * METHODS
	 * ===============================
	 */
	public String getTableName() {
		return mTableName;
	}
	
	/**
	 * Method used to retrieve the DataSource object. The DataSource object is used
	 * for communication between model and database.
	 * @return The instance of the DataSource object.
	 */
	protected DataSource getDataSource() {
		return DataSource.getInstance();
	}
	
	public int isDeleted() {
		return deleted;
	}

	public String getModified() {
		return DateHelper.formatDateTime(mContext, modified);
	}
	
	public String getPlainModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	
	public String getDate() {
		return DateHelper.formatDateTime(mContext, date);
	}
	
	public String getPlainDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * Saves the Model Data to the database.
	 * @return true when saving was successful, false when not.
	 */
	public boolean save() {
		if(this.id > 0) {
			this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mContext.getResources().getConfiguration().locale).format(new Date());
			return mDataSource.edit(this);
		} else {
			this.id = mDataSource.insert(this);
            return this.id > 0;
		}
	}
	
	/**
	 * Soft delete the model.
	 * @return true when deletion was successful, false when not.
	 */
	public boolean delete() {
		if(this.id > 0) {
			this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mContext.getResources().getConfiguration().locale).format(new Date());
			return mDataSource.delete(this);
		} else {
			return false;
		}
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public int compareTo(Model another) {
		try {
			Date a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.getPlainModified());
			Date b = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(another.getPlainModified());
			return a.compareTo(b) * -1;
		} catch (ParseException e) {
			Log.e(getClass().getName(), "Error parsing date for comparation!", e);
			return 0;
		} 
	}
	
	public int getCount() {
		List<? extends Model> items;
		try {
			items = getAll();
			return items.size();
		} catch (ModelNotFoundException e) {
			return 0;
		}
	}


	
	/**
	 * ===============================
	 * ABSTRACT METHODS
	 * ===============================
	 */
	
	/**
	 * Get the data stored in the model wrapped in a ContentValues object,
	 * so it can be used in the DataSource object.
	 * @return The model data in a DataSource object.
	 */
	public abstract ContentValues getData();

	/**
	 * Gets all table columns of the current model.
	 * @return The table columns of the current model in a String Array.
	 */
	public abstract String[] getColumns();

	public abstract List<? extends Model> getAll() throws ModelNotFoundException;

	public abstract DatabaseConfiguration getDatabaseConfiguration();

    /**
     * ===============================
     * STATIC METHODS
     * ===============================
     */

    /**
     * Method to save manyToMany relations in a database. You need to supply the correct models in order
     * for this method to work properly.
     * @param tableName The name of the join table for this relation.
     * @param model1 The first model to save.
     * @param model2 The second model to save.
     * @return true when saving was successful, false when not.
     */
    public static boolean saveManyToMany(String tableName, Model model1, Model model2) {
        long relationId = mDataSource.insertManyToMany(tableName, model1, model2);
        return relationId != 0;
    }
}
