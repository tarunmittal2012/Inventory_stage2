package com.example.tarunmittal.inventory;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.tarunmittal.inventory.Data.InventoryContract;
import com.example.tarunmittal.inventory.Data.InventoryContract.InventoryEntry;
public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {

        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.custom_list, viewGroup, false);

    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView productNameTextView = view.findViewById(R.id.product_name_textview);
        TextView productPriceTextView = view.findViewById(R.id.product_price_textview);
        TextView productQuantityTextView = view.findViewById(R.id.product_quantity_textview);
        Button productSaleButton = view.findViewById(R.id.sale_button);

        final int columnIdIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        final String productID = cursor.getString(columnIdIndex);
        String productName = cursor.getString(productNameColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        final String productQuantity = cursor.getString(productQuantityColumnIndex);

        productSaleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity Activity = (MainActivity) context;
                Activity.productSaleCount(Integer.valueOf(productID), Integer.valueOf(productQuantity));
            }
        });

        productNameTextView.setText("( " + productID + " ) " + productName);
        productPriceTextView.setText(context.getString(R.string.product_price) + " : " + productPrice + "  " + context.getString(R.string.rs));
        productQuantityTextView.setText(context.getString(R.string.product_quantity) + " : " + productQuantity);

        Button productEditButton = view.findViewById(R.id.edit_button);
        productEditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), AddActivity.class);
                Uri currentProdcuttUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, Long.parseLong(productID));
                intent.setData(currentProdcuttUri);
                context.startActivity(intent);
            }
        });

    }
}
