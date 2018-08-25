package com.example.tarunmittal.inventory.Data;

import android.provider.BaseColumns;
public final class InventoryContract  {
    private InventoryContract(){

    }
    public  static final class InventoryEntry implements BaseColumns{

        public final static String TABLE_NAME="product";

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

    }

}
