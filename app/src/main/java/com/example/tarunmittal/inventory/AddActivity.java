package com.example.tarunmittal.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tarunmittal.inventory.Data.InventoryContract;
import com.example.tarunmittal.inventory.Data.InventoryContract.InventoryEntry;
import com.example.tarunmittal.inventory.Data.InventoryDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
public class AddActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = AddActivity.class.getName();

    private static final int EXISTING_INVENTORY_LOADER = 0;

    @BindView(R.id.product_name_edit)
    EditText productName;

    @BindView(R.id.product_price_edit)
    EditText productPriceText;

    @BindView(R.id.product_quantity_edit)
    EditText productQuantityText;

    Spinner mSpinner;

    @BindView(R.id.supplier_phone_edit)
    EditText supplierPhoneNumberText;

    private Uri mCurrentProductUri;

    private boolean isProductChanged = false;

    private int mSupplieName = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            isProductChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();

            loaderManager.initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        mSpinner = findViewById(R.id.product_supplier_spinner);
        productName.setOnTouchListener(mOnTouchListener);
        productPriceText.setOnTouchListener(mOnTouchListener);
        productQuantityText.setOnTouchListener(mOnTouchListener);
        mSpinner.setOnTouchListener(mOnTouchListener);
        supplierPhoneNumberText.setOnTouchListener(mOnTouchListener);

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
            case R.id.save:
                saveProduct();
                return true;
            case android.R.id.home:
                if (!isProductChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        NavUtils.navigateUpFromSameTask(AddActivity.this);
                    }
                };
                showUnsavedChangesDialog(listener);
                return true;


            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                if (!isProductChanged) {
                    NavUtils.navigateUpFromSameTask(AddActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(AddActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!isProductChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
            int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            productName.setText(currentName);
            productPriceText.setText(Integer.toString(currentPrice));
            productQuantityText.setText(Integer.toString(currentQuantity));
            supplierPhoneNumberText.setText(Integer.toString(currentSupplierPhone));

            switch (currentSupplierName) {
                case InventoryEntry.SUPPLIER_AMAZON:
                    mSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_FLIPKART:
                    mSpinner.setSelection(2);
                    break;
                case InventoryEntry.SUPPLIER_EBAY:
                    mSpinner.setSelection(3);
                    break;
                default:
                    mSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        productName.setText("");
        productPriceText.setText("");
        productQuantityText.setText("");
        supplierPhoneNumberText.setText("");
        mSpinner.setSelection(0);

    }

    private void saveProduct() {

        String productNameString = productName.getText().toString().trim();
        String productPriceString = productPriceText.getText().toString().trim();
        String productQuantityString = productQuantityText.getText().toString().trim();
        String productSupplierPhoneNumberString = supplierPhoneNumberText.getText().toString().trim();
        if (mCurrentProductUri == null) {
            if (TextUtils.isEmpty(productNameString)) {
                Toast.makeText(this, getString(R.string.product_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPriceString)) {
                Toast.makeText(this, getString(R.string.price_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSupplieName == InventoryEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.supplier_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.supplier_phone_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues values = new ContentValues();

            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPriceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantityString);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, mSupplieName);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberString);
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {

            if (TextUtils.isEmpty(productNameString)) {
                Toast.makeText(this, getString(R.string.product_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPriceString)) {
                Toast.makeText(this, getString(R.string.price_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSupplieName == InventoryEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.supplier_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.supplier_phone_requires), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPriceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantityString);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, mSupplieName);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberString);

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
