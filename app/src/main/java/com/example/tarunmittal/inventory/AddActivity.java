package com.example.tarunmittal.inventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tarunmittal.inventory.Data.InventoryContract;
import com.example.tarunmittal.inventory.Data.InventoryDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
public class AddActivity extends AppCompatActivity {

    private static final String TAG = AddActivity.class.getName();

    @BindView(R.id.product_name_edit)
    EditText productName;

    @BindView(R.id.product_price_edit)
    EditText productPriceText;

    @BindView(R.id.product_quantity_edit)
    EditText productQuantityText;

    Spinner mSpinner;

    @BindView(R.id.supplier_phone_edit)
    EditText supplierPhoneNumberText;

    private int mSupplieName = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        mSpinner = findViewById(R.id.product_supplier_spinner);
        setUpSpinner();
    }

    private void setUpSpinner() {

        ArrayAdapter supplierAdapter = ArrayAdapter.createFromResource(this, R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        supplierAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mSpinner.setAdapter(supplierAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.amazon))) {
                        mSupplieName = InventoryContract.InventoryEntry.SUPPLIER_AMAZON;
                    } else if (selection.equals(getString(R.string.flipkart))) {
                        mSupplieName = InventoryContract.InventoryEntry.SUPPLIER_FLIPKART;
                    } else if (selection.equals(getString(R.string.ebay))) {
                        mSupplieName = InventoryContract.InventoryEntry.SUPPLIER_EBAY;
                    } else {
                        mSupplieName = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                mSupplieName = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    private void insertItem() {

        String productNameText = productName.getText().toString().trim();
        String quantityText = productQuantityText.getText().toString().trim();
        String priceText = productPriceText.getText().toString().trim();
        String supplierProductText = supplierPhoneNumberText.getText().toString().trim();
        InventoryDbHelper dbHelper = new InventoryDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        if (TextUtils.isEmpty(productNameText)) {
            productName.requestFocus();
            productName.setError("Required!");
        } else if (TextUtils.isEmpty(quantityText)) {
            productQuantityText.requestFocus();
            productQuantityText.setError("Required!");
        } else if (TextUtils.isEmpty(priceText)) {
            productPriceText.requestFocus();
            productPriceText.setError("Required!");
        } else if (TextUtils.isEmpty(supplierProductText)) {
            supplierPhoneNumberText.requestFocus();
            supplierPhoneNumberText.setError("Required!");
        } else {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, productNameText);
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, priceText);
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityText);
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierProductText);
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, mSupplieName);
            long newRowId = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

            if (newRowId == -1) {
                Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
                Log.d("Error message", "Doesn't insert row on table");

            } else {
                Toast.makeText(this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
                Log.d("successfully message", "insert row on table");
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addproduct, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_product:
                insertItem();
                finish();

            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


}
