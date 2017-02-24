package com.interview.interview.interview;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.CartAdapter;
import database.DBAdapter;
import listeners.HidingScrollListener;
import models.Datum;

public class CartPage extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    CartAdapter adapter;
    TextView v_itemCount, v_totalPrice, proceed;
    RelativeLayout main_layout;
    List<Datum> data = new ArrayList<Datum>();
    LinearLayout emptylayout;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page);

        initialise();
    }

    private void initialise() {


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.content_product_actionbar);
        ImageView back = (ImageView) findViewById(R.id.back);
        ImageView cartIcon = (ImageView) findViewById(R.id.carticon);
        cartIcon.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        main_layout = (RelativeLayout) findViewById(R.id.activity_cart_page);
        recyclerView = (RecyclerView) findViewById(R.id.cartRecyclerview);
        progressBar = (ProgressBar) findViewById(R.id.cartprogress);
        main_layout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        emptylayout = (LinearLayout) findViewById(R.id.emptylayout);
        v_itemCount = (TextView) findViewById(R.id.count);
        proceed = (TextView) findViewById(R.id.proceed);
        v_totalPrice = (TextView) findViewById(R.id.totalprice);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proceed.setVisibility(View.VISIBLE);
                    }
                },100);
            }
            @Override
            public void onShow() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proceed.setVisibility(View.GONE);
                    }
                },100);
            }
        });
        fetchData();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.size() > 0){
                    System.out.println("AMOUNT : "+getTotalPrice());
                    Intent payment = new Intent(CartPage.this, Checkout.class);
                    payment.putExtra("Amount", getTotalPrice());
                    startActivity(payment);
                    finish();
                }
                else {
                    Toast.makeText(CartPage.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchData() {
        main_layout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        data = DBAdapter.getInstance(this).getCartList();
        if (data.size() > 0) {
            System.out.println("CART COUNT : " + data.size());
            adapter = new CartAdapter(CartPage.this, data);
            recyclerView.setAdapter(adapter);
            v_itemCount.setText(getResources().getString(R.string.countlabel) + " (" + data.size() + ") ");
            v_totalPrice.setText("Rs." + String.valueOf(getTotalPrice()));
        }
        else {
            emptylayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            v_itemCount.setText(getResources().getString(R.string.countlabel) + " (" + 0 + ") ");
            v_totalPrice.setText("Rs." + String.valueOf(0));
        }
    }


    public void updateUi(int size, long price){
        v_itemCount.setText(getResources().getString(R.string.countlabel) + " (" + size + ") ");
        v_totalPrice.setText("Rs." + String.valueOf(price));
    }

    private long getTotalPrice() {
        long temp = 0;
        for (Datum item : data) {
            temp = temp + Long.parseLong(item.getPrice());
        }
        return temp;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
