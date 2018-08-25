package com.example.tarunmittal.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.tarunmittal.inventory.Data.InventoryContract;
import com.example.tarunmittal.inventory.Data.InventoryDbHelper;

import butterknife.BindView;
import butterknife.OnClick;
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.add_item)
    FloatingActionButton mFloatingActionButton;

    TextView displayItem;

    private InventoryDbHelper helper;

    @OnClick(R.id.add_item)
    public void addItem(View view) {

        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayItem = findViewById(R.id.display_product);

        displayItem.setTextColor(getResources().getColor(R.color.colorAccent));
        helper = new InventoryDbHelper(this);

    }

    @Override
    protected void onStart() {

        super.onStart();
        displayProduct();
    }

    private void displayProduct() {

        SQLiteDatabase database = helper.getReadableDatabase();
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        Cursor cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        try {
            displayItem.setText("Inventory Contains : " + cursor.getCount() + "\n");
            displayItem.append(
                    InventoryContract.InventoryEntry._ID + " | " +
                            InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME + " | " +
                            InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE + " | " +
                            InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY + " | " +
                            InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " | " +
                            InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + "\n");

            int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                displayItem.append(("\n" + currentID + " - " +
                        currentName + " !- " +
                        currentPrice + " !- " +
                        currentQuantity + " !- " +
                        currentSupplierName + "!- " +
                        currentSupplierPhone));
            }
        } finally {
            cursor.close();
        }

    }

}
