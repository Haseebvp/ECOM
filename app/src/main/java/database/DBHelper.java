package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "dash.db";

    public static final String TABLE_PRODUCTS = "Products";
    static final String COL_PROD_ID = "productid";
    static final String COL_PROD_URL = "image";
    static final String COL_PROD_TITLE = "title";
    static final String COL_PROD_DESC = "description";
    static final String COL_PROD_PRICE = "price";
    static final String COL_PROD_COUNT = "count";
    static final String COL_PROD_SIZE = "size";


    public static final String TABLE_CART = "Cart";
    static final String COL_CART_ID = "productid";
    static final String COL_CART_URL = "image";
    static final String COL_CART_TITLE = "title";
    static final String COL_CART_DESC = "description";
    static final String COL_CART_PRICE = "price";
    static final String COL_CART_COUNT = "count";
    static final String COL_CART_SIZE = "size";

    private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + COL_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PROD_TITLE + " TEXT NOT NULL, "
            + COL_PROD_DESC + " TEXT NOT NULL, "
            + COL_PROD_URL + " TEXT NOT NULL, "
            + COL_PROD_PRICE + " TEXT NOT NULL, "
            + COL_PROD_SIZE + " TEXT NOT NULL, "
            + COL_PROD_COUNT + " INTEGRER DEFAULT 0 " + ");";


    private static final String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
            + COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CART_TITLE + " TEXT NOT NULL, "
            + COL_CART_DESC + " TEXT NOT NULL, "
            + COL_CART_URL + " TEXT NOT NULL, "
            + COL_CART_PRICE + " TEXT NOT NULL, "
            + COL_CART_SIZE + " TEXT NOT NULL, "
            + COL_CART_COUNT + " INTEGRER DEFAULT 0 " + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO handle db upgrade for existing users here
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }
}
