package com.artcodix.lib.droidmemory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class is the link between models and database. It holds the database connection
 * as well as its own instance (Singleton Pattern). Before it can be used, you have to
 * initialize it with the current {@link Context}.
 *
 * @author Marco Schweizer
 * @version 0.1
 * @since 29.04.2014
 */

public class DataSource {

	private SQLiteDatabase mDb;
	private DbHelper mDbHelper;
	private static DataSource mInstance;
	private Context mContext;
	private DatabaseConfiguration mConfig;

	private DataSource() {

	}

	/**
	 * Singleton Pattern.
	 *
	 * INIT() MUST BE CALLED AFTER THIS, SO THE CONTEXT CAN BE SET!
	 *
	 * @return the DataSource
	 */
	public static DataSource getInstance() {
		if (mInstance == null) {
			mInstance = new DataSource();
		}

		return mInstance;
	}

	/**
	 * Open the database connection.
	 */
	private void open() {
		mDbHelper = new DbHelper(mContext, mConfig);

		if (mDb == null) {
			mDb = mDbHelper.getWritableDatabase();
		}
	}

	/**
	 * Provide a valid context for the DataSource
	 *
	 * @param context
	 *            The context in which the DataSource is used
	 */
	public void init(Context context, DatabaseConfiguration config) {
		mContext = context.getApplicationContext();
		mConfig = config;
		if(mDb != null) {
			if(!mDb.isOpen()) {
				open();
			}
		} else {
			open();
		}
	}

	/**
	 * Close the database connection after use to save resources.
	 */
	public void close() {
		mDb.close();
		mDbHelper.close();
	}

	/**
	 * Model values will be retrieved and stored in a new database entry. This method is called
	 * when the provided model has no valid ID and is therefore new.
	 * @param model The model holding the information.
	 * @return The new ID when insertion was successful, 0 when not.
	 */
	public long insert(Model model) {
		long id = mDb.insert(model.getTableName(), null, model.getData());

		if(id > 0) {
			return id;
		} else {
			return 0;
		}
	}

    /**
     * Saves a manyToMany relationship. Both models need to be a part of the relation
     * in order for this method to work.
     * @param tableName The name of the relation table.
     * @param model1 The first model to save.
     * @param model2 The second model to save.
     * @return The relationship id.
     */
    public long insertManyToMany(String tableName, Model model1, Model model2) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(model1.getTableName() + "_id", model1.getId());
        insertValues.put(model2.getTableName() + "_id", model2.getId());

        long id = mDb.insert(tableName, null, insertValues);
        if(id > 0) {
            return id;
        } else {
            return 0;
        }
    }

	/**
	 * Model values will be retrieved and stored in an existing database entry. This method is
	 * called when the provided model has a valid ID.
	 * @param model The model holding the information.
	 * @return true when update was successful, false when not.
	 */
	public boolean edit(Model model) {
		int rows = mDb.update(model.getTableName(), model.getData(), "_id = " + model.getId(), null);

		if(rows > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The provided model will be soft deleted from database.
	 * @param model The model to delete.
	 * @return true when deletion was successful, false when not.
	 */
	public boolean delete(Model model) {
		ContentValues values = model.getData();
		values.put("deleted", 1);
		int rows = mDb.update(model.getTableName(), values, "_id = " + model.getId(), null);

        return rows > 0;
	}

	/**
	 * Retrieves the Model data from the database.
	 * @param model An instance of the desired model only holding the ID for reference.
	 * @return A {@link Cursor} containing the model information.
	 */
	public Cursor get(Model model) {
		return mDb.query(model.getTableName(), model.getColumns(), "_id = " + model.getId(), null, null, null, null);
	}

	/**
	 * Retrieves all Models from the database.
	 * @param model An instance of the desired model.
	 * @return A {@link Cursor} containing all the models.
	 */
	public Cursor getAll(Model model) {
		return mDb.query(model.getTableName(), model.getColumns(), null, null, null, null, null);
	}

}
