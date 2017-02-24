package com.interview.interview.interview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CardSchemeCallBack;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Environment;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.widgets.CardNumberEditText;
import com.citrus.widgets.ExpiryDate;

import Utilities.Utils;
import payment.CType;
import payment.Constants;

public class CardActivity extends AppCompatActivity {


    private CardNumberEditText editCardNumber = null;
    private ExpiryDate editExpiryDate = null;
    private EditText editCVV = null, editCardHolderName = null, cardHolderNumber = null;
    private TextView submitButton = null;
    private CType cType = null;
    private Amount amount = null;
    private CitrusClient citrusClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
//
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.content_product_actionbar);
//        ImageView back = (ImageView) findViewById(R.id.back);
//        ImageView cartIcon = (ImageView) findViewById(R.id.carticon);
//        cartIcon.setVisibility(View.INVISIBLE);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        int price  = Utils.getTotalPrice(this);
        amount = new Amount(String.valueOf(price));


        try {
            if (getIntent().getIntExtra("CType", 0) == 0) {
                cType = CType.DEBIT;
            } else {
                cType = CType.CREDIT;
            }
        } catch (Exception e) {
            cType = CType.DEBIT;
        }


        citrusClient = CitrusClient.getInstance(this);
        citrusClient.init(
                "test-signup",
                "c78ec84e389814a05d3ae46546d16d2e",
                "test-signin",
                "52f7e15efd4208cf5345dd554443fd99",
                "testing",
                Environment.SANDBOX );
        editCardNumber = (CardNumberEditText) findViewById(R.id.cardHolderNumber);
        editExpiryDate = (ExpiryDate) findViewById(R.id.cardExpiry);
        editCardHolderName = (EditText) findViewById(R.id.cardHolderName);
        editCVV = (EditText) findViewById(R.id.cardCvv);
        submitButton = (TextView) findViewById(R.id.proceed);

        editCardNumber.getCardScheme(new CardSchemeCallBack() {
            @Override
            public void onCardSchemeReceived(CardOption.CardScheme cardScheme) {
                Log.d("CARDSCHEME", cardScheme.getName());
            }
        });

    }


    public void proceed(View view) {
        String cardHolderName = editCardHolderName.getText().toString();
        String cardNumber = editCardNumber.getText().toString();
        String cardCVV = editCVV.getText().toString();
        Month month = editExpiryDate.getMonth();
        Year year = editExpiryDate.getYear();

        CardOption cardOption;
        if (cType == CType.DEBIT) {
            cardOption = new DebitCardOption(cardHolderName, cardNumber, cardCVV, month, year);
        } else {
            cardOption = new CreditCardOption(cardHolderName, cardNumber, cardCVV, month, year);
        }

        PaymentType paymentType;

        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {

            }

            @Override
            public void error(CitrusError error) {
                System.out.println("ERR : "+error.getMessage());
            }
        };

        try {
            paymentType = new PaymentType.PGPayment(amount, Constants.BILL_URL, cardOption, null);
            citrusClient.simpliPay(paymentType, callback);

        } catch (CitrusException e) {
            e.printStackTrace();
        }
    }

}
