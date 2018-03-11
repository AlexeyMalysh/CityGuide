package cityguide.garren.com.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cityguide.garren.com.R;
import cityguide.garren.com.activities.DetailActivity;
import cityguide.garren.com.models.Result;

public class ResultsAdapter extends ArrayAdapter<Result> {

    private int layoutResourceId;
    private final Context context;
    private final ArrayList<Result> results;

    public ResultsAdapter(@NonNull Context context, int resource, ArrayList<Result> results) {
        super(context, resource, results);

        this.context = context;
        this.layoutResourceId = resource;
        this.results = results;
    }

    public static class ViewHolder {
        public final ImageView icon;
        public final TextView title;
        public final TextView distance;

        public final ImageView rating1;
        public final ImageView rating2;
        public final ImageView rating3;
        public final ImageView rating4;
        public final ImageView rating5;

        public ViewHolder(View view) {
            icon = view.findViewById(R.id.typeIcon);
            title = view.findViewById(R.id.resultTitle);
            distance = view.findViewById(R.id.resultDistance);

            rating1 = view.findViewById(R.id.rating1);
            rating2 = view.findViewById(R.id.rating2);
            rating3 = view.findViewById(R.id.rating3);
            rating4 = view.findViewById(R.id.rating4);
            rating5 = view.findViewById(R.id.rating5);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String resultTitle = results.get(position).getTitle();
        int resultType = results.get(position).getType();
        String resultDistance = results.get(position).getDistance();
        int resultRating = results.get(position).getRating();
        final Location resultLocation = results.get(position).getLocation();

        viewHolder.title.setText(resultTitle);
        viewHolder.distance.setText(resultDistance);
        if(resultType == 0) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_bar));
        } else if (resultType == 1) {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_bistro));
        } else {
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_cafe));
        }

        if (resultRating == 1) {
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
        } else if(resultRating == 2) {
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating2.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
        } else if (resultRating == 3) {
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating2.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating3.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
        } else if (resultRating == 4) {
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating2.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating3.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating4.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
        } else if (resultRating == 5) {
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
            viewHolder.rating1.setImageDrawable(context.getResources().getDrawable(R.mipmap.star_pink));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("name", resultTitle);
                intent.putExtra("latitude", resultLocation.getLatitude());
                intent.putExtra("longitude", resultLocation.getLongitude());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
