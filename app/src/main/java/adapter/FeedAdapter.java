package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.interview.interview.interview.FeedPage;
import com.interview.interview.interview.Product;
import com.interview.interview.interview.R;

import java.util.List;

import Utilities.ExternalFunctions;
import Utilities.Utils;
import database.DBAdapter;
import models.Datum;

/**
 * Created by haseeb on 20/2/17.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public int TYPE_ITEM = 0;
    public int TYPE_NEXT = 1;

    List<Datum> imageList;
    Context context;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, price;
        public Button buy, cart;
        public ImageView image;

        public ItemViewHolder(View itemview) {
            super(itemview);
            title = (TextView) itemview.findViewById(R.id.product_title);
            description = (TextView) itemview.findViewById(R.id.description);
            price = (TextView) itemview.findViewById(R.id.product_price);
            buy = (Button) itemview.findViewById(R.id.buy);
            cart = (Button) itemview.findViewById(R.id.cart);
            image = (ImageView) itemview.findViewById(R.id.productImage);
            title.setTypeface(Utils.NormalCustomTypeface(context));
            description.setTypeface(Utils.NormalCustomTypeface(context));
            cart.setTypeface(Utils.NormalCustomTypeface(context));
            buy.setTypeface(Utils.NormalCustomTypeface(context));
            image.getLayoutParams().height = (int) (ExternalFunctions.getScreenWidth(context)*0.35);
            image.getLayoutParams().width = (int) (ExternalFunctions.getScreenWidth(context)*0.35);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(View itemView){
            super(itemView);
        }
    }



    public FeedAdapter(Context context, List<Datum> imageList) {
        this.imageList = imageList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        if (viewType == TYPE_ITEM) {
            itemView = inflater.inflate(R.layout.feed_item, parent, false);
            return new ItemViewHolder(itemView);
        }
        else {
            itemView = inflater.inflate(R.layout.feed_progress, parent, false);
            return new ProgressViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            final ItemViewHolder holder1 = (ItemViewHolder)holder;
            final Datum item = imageList.get(position);
            holder1.price.setText("Rs."+item.getPrice());
            Glide.with(context)
                    .load(item.getUrl())
                    .centerCrop()
                    .override((int) (ExternalFunctions.getScreenWidth(context)*0.35), (int) (ExternalFunctions.getScreenWidth(context)*0.35))
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(holder1.image);
            holder1.title.setText(item.getTitle());
            holder1.description.setText(item.getDesc());

            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent product = new Intent(context, Product.class);
                    product.putExtra("Url",item.getUrl());
                    context.startActivity(product);
                }
            });

            holder1.cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    DBAdapter.getInstance(context)
                    ((FeedPage) context).ShowSizeDialog(item.getSize().split(","), position, "cart");
                }
            });
            if (item.getCount() != null) {
                if (item.getCount() == 0) {
                    holder1.buy.setText("SOLD");
                    holder1.buy.setEnabled(false);

                    holder1.cart.setVisibility(View.GONE);
                }
            }

            holder1.buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FeedPage) context).ShowSizeDialog(item.getSize().split(","), position, "buy");
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
        if (imageList.get(position) != null){
            return TYPE_ITEM;
        }
        else {
            return TYPE_NEXT;
        }
    }

}