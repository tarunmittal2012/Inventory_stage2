package com.example.tarunmittal.inventory.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
public final class InventoryContract  {

    public static final String CONTENT_AUTHORITY = "com.example.tarunmittal.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "store";

    private InventoryContract(){

    }
    public  static final class InventoryEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "store";

        public final static String _ID=BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME="product_name";
        public final static String COLUMN_PRODUCT_QUANTITY="quantity";
        public final static String COLUMN_PRODUCT_PRICE="price";
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME="spinner";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER="number";

        public final static int SUPPLIER_UNKNOWN=0;
        public final static int SUPPLIER_AMAZON=1;
        public final static int SUPPLIER_FLIPKART=2;
        public final static int SUPPLIER_EBAY=3;

        public static boolean isValidSupplierName(int suppliername) {

            return suppliername == SUPPLIER_UNKNOWN || suppliername == SUPPLIER_AMAZON || suppliername == SUPPLIER_FLIPKART || suppliername == SUPPLIER_EBAY;
        }
    }

}
