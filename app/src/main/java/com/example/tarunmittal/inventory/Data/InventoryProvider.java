package com.example.tarunmittal.inventory.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tarunmittal.inventory.Data.InventoryContract.InventoryEntry;
public class InventoryProvider extends ContentProvider {

    private static final int PROUDCTS = 100;

    private static final int PROUDCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, PROUDCTS);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", PROUDCT_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PROUDCTS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PROUDCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROUDCTS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case PROUDCT_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROUDCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {

        String productName = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Product name requires");
        }

        Integer productPrice = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (productPrice != null && productPrice < 0) {
            throw new IllegalArgumentException("Product price requires valid");
        }

        Integer productQunatity = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQunatity != null && productQunatity < 0) {
            throw new IllegalArgumentException("Product quantity requires valid");
        }

        Integer supplierName = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplierName == null || !InventoryContract.InventoryEntry.isValidSupplierName(supplierName)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        Integer supplierPhoneNum = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNum != null && supplierPhoneNum < 0) {
            throw new IllegalArgumentException("Supplier Phone requires valid");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.v("message:", "Failed to insert new row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int deletedRows;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROUDCTS:
                deletedRows = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PROUDCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROUDCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PROUDCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String nameProduct = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            if (nameProduct == null) {
                throw new IllegalArgumentException("Product name requires");
            }
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE)) {
            Integer priceProduct = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_PRICE);
            if (priceProduct != null && priceProduct < 0) {
                throw new
                        IllegalArgumentException("Product price requires valid");
            }
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantityProduct = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantityProduct != null && quantityProduct < 0) {
                throw new
                        IllegalArgumentException("Product quantity requires valid");
            }
        }
        if (contentValues.containsKey(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            Integer supplierName = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null || !InventoryEntry.isValidSupplierName(supplierName)) {
                throw new IllegalArgumentException("Supplier Name requires valid");
            }
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            Integer supplierPhone = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone != null && supplierPhone < 0) {
                throw new
                        IllegalArgumentException("Supplier Phone requires valid");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

