package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.interview.interview.interview.CartPage;
import com.interview.interview.interview.R;

import java.util.ArrayList;
import java.util.List;

import Utilities.ExternalFunctions;
import Utilities.Utils;
import database.DBAdapter;
import database.DBHelper;
import models.Datum;

/**
 * Created by haseeb on 22/2/17.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public int TYPE_ITEM = 0;
    public int TYPE_NEXT = 1;

    List<Datum> imageList = new ArrayList<Datum>();
    Context context;
    ArrayAdapter<String> dataAdapter;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, price, cartLabel;
        public ImageView image;
        public TextView remove;
        public Spinner spinner;

        public ItemViewHolder(View itemview) {
            super(itemview);
            title = (TextView) itemview.findViewById(R.id.product_title);
            description = (TextView) itemview.findViewById(R.id.description);
            cartLabel = (TextView) itemview.findViewById(R.id.cartLabel);
            price = (TextView) itemview.findViewById(R.id.product_price);
            remove = (TextView) itemview.findViewById(R.id.remove);
            image = (ImageView) itemview.findViewById(R.id.productImage);
            spinner = (Spinner) itemview.findViewById(R.id.sizeSpinner);
//            spinner.setAdapter(dataAdapter);
            title.setTypeface(Utils.NormalCustomTypeface(context));
            description.setTypeface(Utils.NormalCustomTypeface(context));
            image.getLayoutParams().height = (int) (ExternalFunctions.getScreenWidth(context) * 0.35);
            image.getLayoutParams().width = (int) (ExternalFunctions.getScreenWidth(context) * 0.35);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }


    public CartAdapter(Context context, List<Datum> imageList) {
        this.imageList = imageList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        if (viewType == TYPE_ITEM) {
            itemView = inflater.inflate(R.layout.feed_item_cart, parent, false);
            return new ItemViewHolder(itemView);
        } else {
            itemView = inflater.inflate(R.layout.feed_progress, parent, false);
            return new ProgressViewHolder(itemView);
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            final ItemViewHolder holder1 = (ItemViewHolder) holder;
            final Datum item = imageList.get(position);
            holder1.price.setText("Rs." + item.getPrice());
            Glide.with(context)
                    .load(item.getUrl())
                    .centerCrop()
                    .override((int) (ExternalFunctions.getScreenWidth(context) * 0.35), (int) (ExternalFunctions.getScreenWidth(context) * 0.35))
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(holder1.image);
            holder1.title.setText(item.getTitle());
            holder1.description.setText(item.getDesc());

            final List<String> sizes = new ArrayList<String>();
            String [] sizeTemp = DBAdapter.getInstance(context).getProduct(item.getUrl()).getSize().split(",");
            for (int i=0;i<sizeTemp.length;i++) {
                sizes.add(sizeTemp[i]);
            }
            dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, sizes);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            holder1.spinner.setAdapter(dataAdapter);
            holder1.spinner.setSelection(sizes.indexOf(item.getSize()));


            holder1.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!DBAdapter.getInstance(context).cartExists(item.getUrl(), sizes.get(position))){
                        item.setSize(sizes.get(position));
                        DBAdapter.getInstance(context).updateCart(item);
                    }
                    else {
                        Toast.makeText(context, "Size already selected", Toast.LENGTH_SHORT).show();
                        holder1.spinner.setSelection(sizes.indexOf(item.getSize()));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            holder1.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Item is removing...", Toast.LENGTH_SHORT).show();
                    imageList.remove(position);
                    DBAdapter.getInstance(context).removeproduct(item.getUrl(), DBHelper.TABLE_CART);
                    notifyDataSetChanged();
                    ((CartPage)context).updateUi(imageList.size(), getPrice());

                }
            });


            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (imageList.get(position) != null) {
            return TYPE_ITEM;
        } else {
            return TYPE_NEXT;
        }
    }

    public long getPrice(){
        long temp = 0;
        for (Datum item : imageList) {
            temp = temp + Long.parseLong(item.getPrice());
        }
        return temp;
    }
}
