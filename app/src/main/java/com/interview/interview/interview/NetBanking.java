package com.interview.interview.interview;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Environment;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;

import java.util.ArrayList;

import Utilities.Utils;
import adapter.NetbankingAdapter;
import listeners.RecyclerItemClickListener;
import payment.Constants;

public class NetBanking extends AppCompatActivity {


    private ArrayList<NetbankingOption> mNetbankingOptionsList;
    private Amount amount = null;
    private MerchantPaymentOption mMerchantPaymentOption = null;
    NetbankingAdapter netbankingAdapter;
    RecyclerView recylerViewNetbanking;
    CitrusClient citrusClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_bankig);


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

        citrusClient = CitrusClient.getInstance(this);
        citrusClient.init(
                "test-signup",
                "c78ec84e389814a05d3ae46546d16d2e",
                "test-signin",
                "52f7e15efd4208cf5345dd554443fd99",
                "testing",
                Environment.SANDBOX );

        int price = Utils.getTotalPrice(this);
        amount = new Amount(String.valueOf(price));

        fetchBankData();

        netbankingAdapter = new NetbankingAdapter(this, mNetbankingOptionsList);

        recylerViewNetbanking = (RecyclerView) findViewById(R.id.recycler_view_netbanking);
        recylerViewNetbanking.setAdapter(netbankingAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recylerViewNetbanking.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recylerViewNetbanking.setLayoutManager(mLayoutManager);

        recylerViewNetbanking.addOnItemTouchListener(new RecyclerItemClickListener(this, new OnItemClickListener()));

    }

    public void fetchBankData(){
        CitrusClient.getInstance(this).getMerchantPaymentOptions(new Callback<MerchantPaymentOption>() {
            @Override
            public void success(MerchantPaymentOption merchantPaymentOption) {
                mMerchantPaymentOption = merchantPaymentOption;
                netbankingAdapter.setNetbankingOptionList(mMerchantPaymentOption.getNetbankingOptionList());
                netbankingAdapter.notifyDataSetChanged();

                mNetbankingOptionsList = mMerchantPaymentOption.getNetbankingOptionList();
                System.out.println("NETBAKING : "+mNetbankingOptionsList.size());
            }

            @Override
            public void error(CitrusError error) {
                System.out.println("NETBAKING : "+error.getMessage());
            }
        });
    }


    private NetbankingOption getItem(int position) {
        NetbankingOption netbankingOption = null;

        if (mNetbankingOptionsList != null && mNetbankingOptionsList.size() > position && position >= -1) {
            netbankingOption = mNetbankingOptionsList.get(position);
        }

        return netbankingOption;
    }

    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener {

        @Override
        public void onItemClick(View childView, int position) {
            NetbankingOption netbankingOption = getItem(position);
            CitrusClient client = CitrusClient.getInstance(NetBanking.this);
            PaymentType paymentType1;
            Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse) {

                }

                @Override
                public void error(CitrusError error) {
                }
            };

            try {
                paymentType1 = new PaymentType.PGPayment(amount, Constants.BILL_URL, netbankingOption, null);
                client.simpliPay(paymentType1, callback);

            } catch (CitrusException e) {
                e.printStackTrace();
            }
        }
    }
}


