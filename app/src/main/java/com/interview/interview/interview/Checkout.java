package com.interview.interview.interview;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import Utilities.Utils;
import payment.CType;

public class Checkout extends AppCompatActivity {

    TextView amount, amountlabel, adrressheader,paymentheader;
    RadioGroup addressGroup, paymentGroup;
    RadioButton add1,add2,debit, credit, netbanking;

    int Amount;
    CType cType;
    boolean addcheck=false;
    int paymentCheck = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


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
        Amount = Utils.getTotalPrice(this);
        System.out.println("AMOUNT @ : "+Amount);

        Initialise();


    }

    private void Initialise() {
        amountlabel = (TextView) findViewById(R.id.amountlabel);
        amount = (TextView) findViewById(R.id.amount);
        adrressheader = (TextView) findViewById(R.id.adrressheader);
        paymentheader = (TextView) findViewById(R.id.paymentheader);
        addressGroup = (RadioGroup) findViewById(R.id.radioGroup);
        paymentGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        add1 = (RadioButton) findViewById(R.id.radioButton);
        add2 = (RadioButton) findViewById(R.id.radioButton2);
        debit = (RadioButton) findViewById(R.id.debit);
        credit = (RadioButton) findViewById(R.id.credit);
        netbanking = (RadioButton) findViewById(R.id.netbanking);

        amountlabel.setTypeface(Utils.NormalCustomTypeface(this));
        amount.setTypeface(Utils.BoldCustomTypeface(this));
        adrressheader.setTypeface(Utils.BoldCustomTypeface(this));
        paymentheader.setTypeface(Utils.BoldCustomTypeface(this));
        add1.setTypeface(Utils.NormalCustomTypeface(this));
        add2.setTypeface(Utils.NormalCustomTypeface(this));
        debit.setTypeface(Utils.NormalCustomTypeface(this));
        credit.setTypeface(Utils.NormalCustomTypeface(this));
        netbanking.setTypeface(Utils.NormalCustomTypeface(this));

        amount.setText("Rs."+Amount);


        addressGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton:
                        addcheck = true;
                        break;
                    case R.id.radioButton2:
                        addcheck = true;
                        break;
                }
            }
        });


        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.debit:
                        paymentCheck = 1;
                        break;

                    case R.id.credit:
                        paymentCheck = 2;
                        break;

                    case R.id.netbanking:
                        paymentCheck = 3;
                        break;
                }
            }
        });



    }

    public void proceed(View v){

        if (addcheck && paymentCheck > 0){
            switch (paymentCheck){
                case 1:
                    Intent card = new Intent(this, CardActivity.class);
                    card.putExtra("CType", 0);
                    card.putExtra("Amount", Amount);
                    startActivity(card);
                    break;

                case 2:

                    Intent card1 = new Intent(this, CardActivity.class);
                    card1.putExtra("Amount", Amount);
                    card1.putExtra("CType", 1);
                    startActivity(card1);
                    break;

                case 3:

                    Intent card2 = new Intent(this, NetBanking.class);
                    card2.putExtra("Amount", Amount);
                    startActivity(card2);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent cart = new Intent(Checkout.this, CartPage.class);
        startActivity(cart);
        finish();

    }
}
