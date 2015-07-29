# droid-memory
Droid memory is an Android SQLite abstraction layer inspired by modern web technologies.

## Getting started

You can use *Droid Memory* as a **library project** at this time. Gradle integration will be provided later on.
In order to use it as a library project you need to clone the repository and hook it into your project.

*On Android Studio:*

File > New > Import Module

## How to use it

### Prerequisites

In order to use Droid Memory you need to extend from `DatabaseConfiguration` and add your table definitions as contants that are prefixed with `TABLE_`.

Example:

```java
private String TABLE_EXERCISE = "create table " + Exercise.TABLE_NAME + "(" +
            Model.COLUMN_ID + " integer primary key autoincrement, " +
            Model.COLUMN_DATE + " text " +
            Model.COLUMN_DELETED + " integer, " +
            Model.COLUMN_MODIFIED + " text, " +
            Exercise.COLUMN_NAME + " text not null, " +
            Exercise.COLUMN_SETS + " integer not null, " +
            Exercise.COLUMN_REPS + " integer not null, " +
            Exercise.COLUMN_AUTHOR + " text);";
```

For the moment it is recommended to build an abstract class that extends from `Model` and implements the `getDatabaseConfiguration()` method where you can set your database configuration:

```java
public abstract class MyModel extends Model {
	//...

	public DatabaseConfiguration getDatabaseConfiguration() {
		MyConfiguration config = new MyConfiguration();
		config.setDatabaseName("mydatabase.db");
		config.setDatabaseVersion(1);
	}
}
```

By adding this abstract class you can now extend from it and don't need to bother providing the configuration to your models. I know that this is not the optimal way to do this and I am still thinking of a better solution.

### How to work with Droid Memory

You can create a new Model by extending from your own `Model` class and implementing the missing abstract methods. Those are specific to your table definition, so you need to do it yourself. However most of the querying work is already done for you.

Example:

```java
public class Exercise extends MyModel {

    public static final String TABLE_NAME = "exercise";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_SETS = "sets";
    public static final String COLUMN_AUTHOR = "author";

    private String name;
    private int sets;
    private int reps;
    private String author;

    private DataSource mDs;

    private String[] allColumns = {Model.COLUMN_ID, Model.COLUMN_DATE, Model.COLUMN_DELETED, Model.COLUMN_MODIFIED, COLUMN_NAME, COLUMN_SETS, COLUMN_REPS, COLUMN_AUTHOR};

    public Exercise(Context context) {
        super(TABLE_NAME, context);
        mDs = getDataSource();
        mDs.init(context);
    }

    public Exercise(long id, Context context) throws ModelNotFoundException, ModelDeletedException {
        super(TABLE_NAME, context);
        this.id = id;
        mDs = getDataSource();
        mDs.init(context);

        Cursor cursor = mDs.get(this);
        if(cursor == null) {
            throw new ModelNotFoundException();
        } else {
            cursor.moveToFirst();
            //CHECK FOR DELETION
            if(cursor.getInt(2) == 1) {
                throw new ModelDeletedException();
            }

            this.date = cursor.getString(1);
            this.deleted = cursor.getInt(2);
            this.modified = cursor.getString(3);
            this.name = cursor.getString(4);
            this.sets = cursor.getInt(5);
            this.reps = cursor.getInt(6);
            this.author = cursor.getString(7);
        }
    }

    //Getters and Setters omitted...

    @Override
    public ContentValues getData() {
        ContentValues values = new ContentValues();
        values.put(Exercise.COLUMN_DATE, date);
        values.put(Exercise.COLUMN_MODIFIED, modified);
        values.put(Exercise.COLUMN_NAME, name);
        values.put(Exercise.COLUMN_SETS, sets);
        values.put(Exercise.COLUMN_REPS, reps);
        values.put(Exercise.COLUMN_AUTHOR, author);

        return values;
    }

    @Override
    public String[] getColumns() {
        return allColumns;
    }

    @Override
    public List<? extends Model> getAll() throws ModelNotFoundException {
        Cursor models = mDs.getAll(this);

        if(models == null) {
            Log.e(((Object) this).getClass().getName(), "Models " + ((Object) this).getClass().getName() + " could not be found.");
            throw new ModelNotFoundException();
        }

        models.moveToFirst();

        List<Exercise> ret = new ArrayList<Exercise>();
        while (!models.isAfterLast()) {

            if(models.getInt(2) == 1) {
                models.moveToNext();
                continue;
            }

            Exercise exer = null;
            try {
                exer = new Exercise(models.getLong(0), mContext);
            } catch (ModelDeletedException e) {
                Log.e(((Object) this).getClass().getName(), "Model " + ((Object) this).getClass().getName() + " with id " + id + "could not be found.");
                continue;
            }
            ret.add(exer);
            models.moveToNext();
        }

        return ret;
    }
}

```

The `getData()` method is especially important as it parses the values stored in your object to `ContentValues` object that SQLite needs for its queries. Now you see why this needs to be done by hand once. After that it's just a matter of `model.setColumn(value)` and `model.save()`.

## Things to improve on

Many. This is really just a prototype in dire need of improvement. However: Small projects might be able to make use of it if they don't need to manage lots of data and can work with what is provided at the moment. I will make a list of the problems I already encountered shortly.