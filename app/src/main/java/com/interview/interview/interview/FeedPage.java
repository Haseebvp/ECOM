package com.interview.interview.interview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Utilities.ExternalFunctions;
import Utilities.NetworkUtil;
import Utilities.Utils;
import adapter.FeedAdapter;
import database.DBAdapter;
import database.DBHelper;
import models.AlbumData;
import models.Datum;
import network.ApiClient;
import network.ApiInterface;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 0;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    FeedAdapter adapter;
    int PAGE_NO = 1;
    ApiInterface apiInterface;
    Subscription subscription;
    LinearLayout errorLayout;
    TextView retry, error, heading;
    LinearLayoutManager layoutManager;
    List<Datum> imagedata = new ArrayList<Datum>();
    Boolean next = false, isLoading = true;
    ImageView carticon;
    int totalItemCount = 0, visibleItemCount = 0, firstVisibleItem = 0, previousTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ExternalFunctions.setNext(false, this);

        Initialise();

    }

    private void fetchData() {
        String url = "app/json/?page=" + PAGE_NO;

        apiInterface = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Observable<AlbumData> dataList = apiInterface.getData(url);
        dataList.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        subscription = dataList.subscribe(new Observer<AlbumData>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("RESULT : " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (PAGE_NO == 1) {
                            progressBar.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                        } else {
                            imagedata.remove(imagedata.size() - 1);
                            adapter.notifyItemRemoved(imagedata.size() - 1);
//                            ShowErrorDialog();
                        }
                    }
                });
            }

            @Override
            public void onNext(final AlbumData albumData) {
                System.out.println("RESULT : " + albumData.getMessage() + "---" + albumData.getData().size());
                if (albumData.getMessage().equals("success")) {
                    isLoading = false;
                    next = albumData.getNext();
                    ExternalFunctions.setNext(next, FeedPage.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DisplayData(albumData.getData(), 1);
                            System.out.println("PAGE NO : " + PAGE_NO + "---" + imagedata.size());
                        }
                    });
                    AddToDb(albumData.getData());
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

    }

    public void ShowSizeDialog(final String[] sizeList, final int position, final String param) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.size_select_alert, null);
        LinearLayout sizeholder = (LinearLayout) dialogView.findViewById(R.id.sizeholder);

        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        for (int i = 0; i < sizeList.length; i++) {
            final TextView textView = new TextView(this);
            textView.setText(sizeList[i]);
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(Utils.BoldCustomTypeface(this));
            textView.setPadding(20, 20, 20, 20);
            final int finalI = i;
            final int finalI1 = i;
            final int finalI2 = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!DBAdapter.getInstance(FeedPage.this).cartExists(imagedata.get(position).getUrl(), sizeList[finalI1])) {
                        Datum data = new Datum();
                        data.setTitle(imagedata.get(position).getTitle());
                        data.setUrl(imagedata.get(position).getUrl());
                        data.setSize(sizeList[finalI2]);
                        data.setPrice(imagedata.get(position).getPrice());
                        data.setCount(1);
                        data.setDesc(imagedata.get(position).getDesc());
                        DBAdapter.getInstance(FeedPage.this).addCart(data);
                        alertDialog.dismiss();
                        if (param.equals("buy")) {
                            Intent cart = new Intent(FeedPage.this, CartPage.class);
                            startActivity(cart);
                        } else {
                            Toast.makeText(FeedPage.this, imagedata.get(position).getTitle() + " with size " + sizeList[finalI] + " added to cart", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        if (param.equals("buy")) {
                            Intent cart = new Intent(FeedPage.this, CartPage.class);
                            startActivity(cart);
                        } else {
                            Toast.makeText(FeedPage.this, "Size " + sizeList[finalI] + " already in cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            sizeholder.addView(textView);
        }
        alertDialog.show();
    }

    private void Retry() {
        errorLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        serveFunc();
    }

    private void DisplayData(List<Datum> data, int param) {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
        }


        if (param == 1) {
            if (PAGE_NO == 1) {
                DBAdapter.getInstance(FeedPage.this).clearDatabase(DBHelper.TABLE_PRODUCTS);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                imagedata = data;
                adapter = new FeedAdapter(FeedPage.this, imagedata);
                recyclerView.setAdapter(adapter);
            } else {
                imagedata.remove(imagedata.size() - 1);
                adapter.notifyItemRemoved(imagedata.size() - 1);
                imagedata.addAll(data);
                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            imagedata = data;
            adapter = new FeedAdapter(FeedPage.this, imagedata);
            recyclerView.setAdapter(adapter);
        }
    }

    private void Initialise() {

        carticon = (ImageView) findViewById(R.id.carticon);
        carticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart = new Intent(FeedPage.this, CartPage.class);
                startActivity(cart);
            }
        });

        errorLayout = (LinearLayout) findViewById(R.id.errorLayout);
        retry = (TextView) findViewById(R.id.retry);
        error = (TextView) findViewById(R.id.errormessage);
        error.setTypeface(Utils.NormalCustomTypeface(this));
        retry.setTypeface(Utils.NormalCustomTypeface(this));
        recyclerView = (RecyclerView) findViewById(R.id.recyvlerview);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                totalItemCount = layoutManager.getItemCount();
                visibleItemCount = layoutManager.getChildCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                System.out.println("PAGE NO : " + PAGE_NO + "---" + imagedata.size() + "--" + layoutManager.findLastCompletelyVisibleItemPosition());
//                if (isLoading) {
//                    if (totalItemCount > previousTotal) {
//                        isLoading = false;
//                        previousTotal = totalItemCount;
//                    }
//                }

                if (!isLoading && ((imagedata.size() - 1) == layoutManager.findLastCompletelyVisibleItemPosition())) {
                    if (ExternalFunctions.getNext(FeedPage.this) && PAGE_NO < 3) {
                        System.out.println("SCROLL");
                        PAGE_NO = PAGE_NO + 1;
                        imagedata.add(null);
                        recyclerView.post(new Runnable() {
                            public void run() {
                                adapter.notifyItemInserted(imagedata.size() - 1);
                            }
                        });
                        if (NetworkUtil.getConnectivityStatus(FeedPage.this) > 0) {
                            fetchData();
                        } else {
                            Toast.makeText(FeedPage.this, "Not connected to Internet", Toast.LENGTH_SHORT).show();
                        }
                        isLoading = true;
                    }
                }
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retry();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Utils.permissionCheck(FeedPage.this, Manifest.permission.INTERNET)) {
                if (Utils.permissionCheck(FeedPage.this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                    serveFunc();
                } else {
                    requestPermission(Manifest.permission.ACCESS_NETWORK_STATE, MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
                }
            } else {
                requestPermission(Manifest.permission.INTERNET, MY_PERMISSIONS_REQUEST_INTERNET);
            }
        } else {
            serveFunc();
        }

    }

    private void requestPermission(String param, int temp) {
        ActivityCompat.requestPermissions(FeedPage.this,
                new String[]{param},
                temp);
    }

    private void serveFunc() {
        imagedata.clear();
        System.out.println("DATA : servefunc : " + DBAdapter.getInstance(FeedPage.this).getRecordCount(DBHelper.TABLE_PRODUCTS));
        if (DBAdapter.getInstance(FeedPage.this).getRecordCount(DBHelper.TABLE_PRODUCTS) >= 10) {
            fetchFromDb();
        } else {
            if (NetworkUtil.getConnectivityStatus(FeedPage.this) > 0) {
                fetchData();
            } else {
                Toast.makeText(FeedPage.this, "Not connected to Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchFromDb() {
        System.out.println("DATA : ");
        List<Datum> data = DBAdapter.getInstance(FeedPage.this).getProductList();
        System.out.println("DATA : " + data.size());
        if (data.size() <= 20 && data.size() > 10) {
            PAGE_NO = 2;
            isLoading = false;

        }
        DisplayData(data, 2);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.feed_page, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            Intent cart = new Intent(FeedPage.this, CartPage.class);
            startActivity(cart);
            // Handle the camera action
        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void AddToDb(final List<Datum> data) {
        System.out.println("INSIDE ADD TO DB");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < data.size(); i++) {
                    final int finalI = i;
                    Log.d("DATABASE", "INSIDE ADD TO DB runnable");
                    if (!DBAdapter.getInstance(FeedPage.this).productCheck(data.get(finalI).getUrl())) {
                        DBAdapter.getInstance(FeedPage.this)
                                .addDProduct(data.get(finalI));
                    }
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!Utils.permissionCheck(FeedPage.this, Manifest.permission.ACCESS_NETWORK_STATE)){
                        requestPermission(Manifest.permission.ACCESS_NETWORK_STATE, MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
                    }
                    else {
                        serveFunc();
                    }
                } else {
                }
                break;
            case MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE:
                serveFunc();
                break;


        }
    }
}
