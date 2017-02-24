package com.interview.interview.interview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import Utilities.ExternalFunctions;
import Utilities.Utils;
import database.DBAdapter;
import models.Datum;

public class Product extends AppCompatActivity {

    String imgUrl;
    ImageView productImage;
    TextView title, desc, more, descExtra, price;
    LinearLayout sizeLayout, main_layout;
    ProgressBar product_progress;
    Button cart, buy;
    Datum data;
    String selectedSize = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        imgUrl = getIntent().getStringExtra("Url");
        System.out.println("INSIDE PRODUCT PAGE : "+imgUrl);

        initialise();

    }

    private void initialise() {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.content_product_actionbar);
        ImageView back = (ImageView) findViewById(R.id.back);
        ImageView cartIcon = (ImageView) findViewById(R.id.carticon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartpage = new Intent(Product.this, CartPage.class);
                startActivity(cartpage);
            }
        });

        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        main_layout.setVisibility(View.INVISIBLE);
        product_progress = (ProgressBar) findViewById(R.id.product_progress);
        productImage = (ImageView) findViewById(R.id.product);
        title = (TextView) findViewById(R.id.title);
        desc = (TextView) findViewById(R.id.description);
        more = (TextView) findViewById(R.id.more);

        cart = (Button) findViewById(R.id.cart);
        buy = (Button) findViewById(R.id.buy);


        String mystring=new String("more details");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        more.setText(content);

        descExtra = (TextView) findViewById(R.id.extra_desc);
        price = (TextView) findViewById(R.id.price);
        sizeLayout = (LinearLayout) findViewById(R.id.size_Layout);
        title.setTypeface(Utils.NormalCustomTypeface(this));
        desc.setTypeface(Utils.NormalCustomTypeface(this));
        more.setTypeface(Utils.NormalCustomTypeface(this));
        price.setTypeface(Utils.NormalCustomTypeface(this));
        descExtra.setTypeface(Utils.NormalCustomTypeface(this));
        productImage.getLayoutParams().height = ExternalFunctions.getScreenWidth(this);
        productImage.getLayoutParams().width = ExternalFunctions.getScreenWidth(this);

        descExtra.setVisibility(View.GONE);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descExtra.getVisibility() == View.VISIBLE){
                    descExtra.setVisibility(View.GONE);
                    String mystring=new String("more details");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    more.setText(content);
                }
                else {
                    descExtra.setVisibility(View.VISIBLE);
                    String mystring=new String("less details");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    more.setText(content);
                }
            }
        });
        fetchdata();
    }

    private void fetchdata() {
        try {
            data = DBAdapter.getInstance(Product.this).getProduct(imgUrl);
        }
        catch (Exception e){
            System.out.println("ERRRR : "+e.getMessage());
        }


        DisplayData(data);
    }

    private void DisplayData(Datum data) {
        System.out.println("DAAAA : "+data.getTitle()+"--"+data.getPrice());
        product_progress.setVisibility(View.GONE);
        main_layout.setVisibility(View.VISIBLE);


        Glide.with(this)
                .load(data.getUrl())
                .centerCrop()
                .override(ExternalFunctions.getScreenWidth(this), ExternalFunctions.getScreenWidth(this))
                .placeholder(R.drawable.placeholder)
                .crossFade()
                .into(productImage);

        title.setText(data.getTitle());
        desc.setText(data.getDesc());
        price.setText("Rs."+data.getPrice());
        if (data.getCount() == 0){
            buy.setEnabled(false);
            cart.setEnabled(false);
            buy.setText("SOLD");
        }

        DisplaySizes();

    }
//    int index = 0;
    private void DisplaySizes() {
        sizeLayout.removeAllViews();
        final String [] sizes = data.getSize().split(",");
//        index = sizes.length;


        for (int i=0; i<sizes.length; i++){
            View size = getLayoutInflater().inflate(R.layout.size_view, null);
            final Button sizeButton = (Button) size.findViewById(R.id.sizebutton);
            sizeButton.setText(sizes[i]);

            if (sizes[i].equals(selectedSize)){
                sizeButton.setBackground(Utils.getDrawable(Product.this, R.drawable.clicked_button));
                sizeButton.setTextColor(Color.WHITE);
                selectedSize = sizes[i];
            }

            final int finalI = i;
            sizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sizeButton.setBackground(Utils.getDrawable(Product.this, R.drawable.clicked_button));
                    sizeButton.setTextColor(Color.WHITE);
                    selectedSize = sizes[finalI];
//                    index = finalI;

                    cart.setText("Add to Cart");
                    cart.setBackground(Utils.getDrawable(Product.this, R.drawable.cart_button_bg));
                    cart.setTextColor(Color.BLACK);
                    DisplaySizes();

                }
            });

            sizeLayout.addView(size);
        }
    }


    public void Cart(View v){
        if (selectedSize != null) {
            if (!cart.getText().toString().equals("Go to Cart")) {
                if (!DBAdapter.getInstance(this).cartExists(data.getUrl(),selectedSize)) {
                    cart.setText("Go to Cart");
                    cart.setBackground(Utils.getDrawable(Product.this, R.drawable.cart_clicked));
                    cart.setTextColor(Color.WHITE);
                    Datum temp = new Datum();
                    temp.setTitle(data.getTitle());
                    temp.setDesc(data.getDesc());
                    temp.setSize(selectedSize);
                    temp.setCount(1);
                    temp.setPrice(data.getPrice());
                    temp.setUrl(data.getUrl());
                    DBAdapter.getInstance(this).addCart(temp);
                    Toast.makeText(Product.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Product.this, "Item already exists", Toast.LENGTH_SHORT).show();
                }


            }
            else {
                Intent cart = new Intent(Product.this, CartPage.class);
                startActivity(cart);

            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Select size", Toast.LENGTH_LONG);
        }
    }


    public void Buy(View v){
//        Toast.makeText(getApplicationContext(), "Going to buy", Toast.LENGTH_LONG).show();
        if (!DBAdapter.getInstance(this).cartExists(data.getUrl(),selectedSize)) {
            Datum temp = new Datum();
            temp.setTitle(data.getTitle());
            temp.setDesc(data.getDesc());
            temp.setSize(selectedSize);
            temp.setCount(1);
            temp.setPrice(data.getPrice());
            temp.setUrl(data.getUrl());
            DBAdapter.getInstance(this).addCart(temp);
//            Toast.makeText(Product.this, "Item added to cart", Toast.LENGTH_SHORT).show();

            Intent cart = new Intent(Product.this, CartPage.class);
            startActivity(cart);
        }
        else {
            Intent cart = new Intent(Product.this, CartPage.class);
            startActivity(cart);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

