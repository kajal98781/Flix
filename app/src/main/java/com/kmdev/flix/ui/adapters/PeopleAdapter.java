package com.kmdev.flix.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kmdev.flix.R;
import com.kmdev.flix.RestClient.ApiUrls;
import com.kmdev.flix.models.ResponsePeople;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Kajal on 1/22/2017.
 */
public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private ViewHolder mViewHolder;
    private List<ResponsePeople.ResultsBean> mPeopleList;
    private boolean mIsKnown;

    public PeopleAdapter(List<ResponsePeople.ResultsBean> peopleList, boolean isVisibleknown) {
        mPeopleList = peopleList;
        mIsKnown = isVisibleknown;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_popular_movie, parent, false);
        mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!mIsKnown) {
            if (!TextUtils.isEmpty(mPeopleList.get(position).getProfile_path())) {
                Picasso.with(holder.itemView.getContext())
                        .load(ApiUrls.IMAGE_PATH_ULTRA + mPeopleList.get(position).getProfile_path())
                        .placeholder(R.color.photo_placeholder)   // optional
                        .error(R.color.photo_placeholder)      // optional
                        .into(holder.imageView);
            }
            holder.tvTitle.setText(mPeopleList.get(position).getName());
            holder.tvTitle.setPadding(5, 5, 5, 5);
            holder.tvRate.setText(String.valueOf(mPeopleList.get(position).getPopularityInt()) + "%");
            holder.tvReleaseDate.setVisibility(View.GONE);
        } else {
            List<ResponsePeople.ResultsBean.KnownForBean> knownForBeanList = mPeopleList.get(position).getKnown_for();
            for (int i = 0; i < knownForBeanList.size(); i++) {
                Picasso.with(holder.itemView.getContext())
                        .load(ApiUrls.IMAGE_PATH_ULTRA + knownForBeanList.get(i).getPoster_path())
                        .placeholder(R.color.photo_placeholder)   // optional
                        .error(R.color.photo_placeholder)      // optional
                        .into(holder.imageView);

                holder.tvTitle.setText(knownForBeanList.get(i).getOriginal_title());
                holder.tvTitle.setPadding(0, 0, 0, 5);
                holder.tvRate.setText(String.valueOf(knownForBeanList.get(i).getPopularityInt()));
                holder.tvReleaseDate.setVisibility(View.GONE);
            }
         /*   SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
            try {
                Date date = simpleDateFormat1.parse(mPeopleList.get(position).ge);
                String releaseDate = simpleDateFormat.format(date);
                Date formattedDate = simpleDateFormat.parse(releaseDate);
                holder.tvReleaseDate.setText(simpleDateFormat.format(formattedDate));

            } catch (ParseException e) {
                e.printStackTrace();
            }    */

        }
/*

*/


    }

    @Override
    public int getItemCount() {
        return mPeopleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tvTitle, tvReleaseDate, tvRate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvReleaseDate = (TextView) itemView.findViewById(R.id.tv_release_date);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            tvRate = (TextView) itemView.findViewById(R.id.tv_rating);
        }
    }


}

