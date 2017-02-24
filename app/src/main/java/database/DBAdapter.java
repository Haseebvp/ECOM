package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;

import models.Datum;


public class DBAdapter {

    //TODO Cursors have not been closed. Please close it.

    private static DBAdapter singleInstance;
    private static Object lockObject = new Object();
    private Context context;

    private SQLiteDatabase db = null;

    private DBAdapter(Context context) {
        this.context = context;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static DBAdapter getInstance(Context context) {
        if (singleInstance == null) {
            synchronized (DBAdapter.class) {
                if (singleInstance == null) {
                    singleInstance = new DBAdapter(context);
                }
            }
        }
        return singleInstance;
    }

    public long addDProduct(Datum data) {
        System.out.println("INSIDE DB ADAPTER ADD PRODUCT : " + data.getTitle());
        if (data == null) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_PROD_TITLE, data.getTitle());
        values.put(DBHelper.COL_PROD_DESC, data.getDesc());
        values.put(DBHelper.COL_PROD_PRICE, data.getPrice());
        values.put(DBHelper.COL_PROD_URL, data.getUrl());
        values.put(DBHelper.COL_PROD_SIZE, "XS,S,M,L,XL");
        values.put(DBHelper.COL_PROD_COUNT, 5);

        if (!productExists(data.getUrl())) {
            long rowId = db.insert(DBHelper.TABLE_PRODUCTS, null, values);
            return rowId;
        }

        return -1;

    }
    public long addCart(Datum data) {
        System.out.println("INSIDE DB ADAPTER ADD PRODUCT : " + data.getTitle());
        if (data == null) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_CART_TITLE, data.getTitle());
        values.put(DBHelper.COL_CART_DESC, data.getDesc());
        values.put(DBHelper.COL_CART_PRICE, data.getPrice());
        values.put(DBHelper.COL_CART_URL, data.getUrl());
        values.put(DBHelper.COL_CART_SIZE, data.getSize());
        values.put(DBHelper.COL_CART_COUNT, data.getCount());

        if (!cartExists(data.getUrl(), data.getSize())) {
            long rowId = db.insert(DBHelper.TABLE_CART, null, values);
            return rowId;
        }

        return -1;

    }

    public void updateProduct(Datum data) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_PROD_TITLE, data.getTitle());
        values.put(DBHelper.COL_PROD_DESC, data.getDesc());
        values.put(DBHelper.COL_PROD_PRICE, data.getPrice());
        values.put(DBHelper.COL_PROD_URL, data.getUrl());
        values.put(DBHelper.COL_PROD_SIZE, data.getSize());
        values.put(DBHelper.COL_PROD_COUNT, data.getCount());
        db.update(DBHelper.TABLE_PRODUCTS, values, DBHelper.COL_PROD_URL + " = ?", new String[]{data.getUrl()});
//        db.close();
    }

    public void updateCart(Datum data) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_CART_TITLE, data.getTitle());
        values.put(DBHelper.COL_CART_DESC, data.getDesc());
        values.put(DBHelper.COL_CART_PRICE, data.getPrice());
        values.put(DBHelper.COL_CART_URL, data.getUrl());
        values.put(DBHelper.COL_CART_SIZE, data.getSize());
        values.put(DBHelper.COL_CART_COUNT, data.getCount());
        db.update(DBHelper.TABLE_CART, values, DBHelper.COL_CART_URL + " = ?", new String[]{data.getUrl()});
//        db.close();
    }

    public int getRecordCount(String param) {
        String countQuery = "SELECT  * FROM " + param;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }


    public boolean productCheck(String url) {
        String countQuery = "SELECT * FROM " + DBHelper.TABLE_PRODUCTS + " WHERE " + DBHelper.COL_PROD_URL + " = '" + url + "'";
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        if (cnt == 0) {
            return false;
        } else {
            return true;
        }

    }

    public ArrayList<Datum> getProductList() {
        ArrayList<Datum> products = null;

        Cursor cursor = db.query(DBHelper.TABLE_PRODUCTS, null, null, null, null, null,
                DBHelper.COL_PROD_URL);

        if (cursor != null) {
            products = new ArrayList<>();
        } else {
            return products;
        }

        int idIndex = cursor.getColumnIndex(DBHelper.COL_PROD_ID);
        int titleIndex = cursor.getColumnIndex(DBHelper.COL_PROD_TITLE);
        int descIndex = cursor.getColumnIndex(DBHelper.COL_PROD_DESC);
        int urlIndex = cursor.getColumnIndex(DBHelper.COL_PROD_URL);
        int sizeIndex = cursor.getColumnIndex(DBHelper.COL_PROD_SIZE);
        int priceIndex = cursor.getColumnIndex(DBHelper.COL_PROD_PRICE);
        int countIndex = cursor.getColumnIndex(DBHelper.COL_PROD_COUNT);

        while (cursor.moveToNext()) {
            Datum product = new Datum();
            product.setId(cursor.getInt(idIndex));
            product.setTitle(cursor.getString(titleIndex));
            product.setDesc(cursor.getString(descIndex));
            product.setUrl(cursor.getString(urlIndex));
            product.setPrice(cursor.getString(priceIndex));
            product.setSize(cursor.getString(sizeIndex));
            product.setCount(cursor.getInt(countIndex));
            products.add(product);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return products;
    }

    public ArrayList<Datum> getCartList() {
        ArrayList<Datum> products = null;

        Cursor cursor = db.query(DBHelper.TABLE_CART, null, null, null, null, null,
                DBHelper.COL_CART_URL);

        if (cursor != null) {
            products = new ArrayList<>();
        } else {
            return products;
        }

        int idIndex = cursor.getColumnIndex(DBHelper.COL_CART_ID);
        int titleIndex = cursor.getColumnIndex(DBHelper.COL_CART_TITLE);
        int descIndex = cursor.getColumnIndex(DBHelper.COL_CART_DESC);
        int urlIndex = cursor.getColumnIndex(DBHelper.COL_CART_URL);
        int sizeIndex = cursor.getColumnIndex(DBHelper.COL_CART_SIZE);
        int priceIndex = cursor.getColumnIndex(DBHelper.COL_CART_PRICE);
        int countIndex = cursor.getColumnIndex(DBHelper.COL_CART_COUNT);

        while (cursor.moveToNext()) {
            Datum product = new Datum();
            product.setId(cursor.getInt(idIndex));
            product.setTitle(cursor.getString(titleIndex));
            product.setDesc(cursor.getString(descIndex));
            product.setUrl(cursor.getString(urlIndex));
            product.setPrice(cursor.getString(priceIndex));
            product.setSize(cursor.getString(sizeIndex));
            product.setCount(cursor.getInt(countIndex));
            products.add(product);

        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return products;
    }


    public Datum getProduct(String url) {

        Datum product = null;

        Cursor cursor = db.query(DBHelper.TABLE_PRODUCTS, null, DBHelper.COL_PROD_URL + "=?",
                new String[]{url}, null, null, DBHelper.COL_PROD_URL);

        if (cursor != null) {
            product = new Datum();
        } else {
            return product;
        }

        int idIndex = cursor.getColumnIndex(DBHelper.COL_PROD_ID);
        int titleIndex = cursor.getColumnIndex(DBHelper.COL_PROD_TITLE);
        int descIndex = cursor.getColumnIndex(DBHelper.COL_PROD_DESC);
        int urlIndex = cursor.getColumnIndex(DBHelper.COL_PROD_URL);
        int priceIndex = cursor.getColumnIndex(DBHelper.COL_PROD_PRICE);
        int countIndex = cursor.getColumnIndex(DBHelper.COL_PROD_COUNT);
        int sizeIndex = cursor.getColumnIndex(DBHelper.COL_PROD_SIZE);

        if (cursor.moveToNext()) {
            product.setId(cursor.getInt(idIndex));
            product.setTitle(cursor.getString(titleIndex));
            product.setDesc(cursor.getString(descIndex));
            product.setUrl(cursor.getString(urlIndex));
            product.setPrice(cursor.getString(priceIndex));
            product.setCount(cursor.getInt(countIndex));
            product.setSize(cursor.getString(sizeIndex));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return product;
    }

    public boolean removeproduct(String url, String param) {
        if (param.equals(DBHelper.TABLE_PRODUCTS)) {
            int rowsAffected = db.delete(param, DBHelper.COL_PROD_URL + "=?"
                    , new String[]{url});
            return (rowsAffected > 0);
        }
        else {
            int rowsAffected = db.delete(param, DBHelper.COL_CART_URL + "=?"
                    , new String[]{url});
            return (rowsAffected > 0);
        }
    }

    public boolean productExists(String url) {
        Cursor cursor = db.query(DBHelper.TABLE_PRODUCTS, null, DBHelper.COL_PROD_URL + "=?", new
                String[]{url}, null, null, null);

        return (cursor.getCount() > 0);
    }

    public boolean CartExists(String url) {
        Cursor cursor = db.query(DBHelper.TABLE_PRODUCTS, null, DBHelper.COL_PROD_URL + "=?", new
                String[]{url}, null, null, null);

        return (cursor.getCount() > 0);
    }
    public boolean cartExists(String url, String size) {
        Cursor cursor = db.query(DBHelper.TABLE_CART, null, DBHelper.COL_PROD_URL + "= ? AND "+DBHelper.COL_CART_SIZE + "= ?", new
                String[]{url, size}, null, null, null);

        return (cursor.getCount() > 0);
    }

    public int clearDatabase(String param) {
        int rowsAffected = db.delete(param, null, null);
        return rowsAffected;
    }

}
