package fraternityandroid.greeklife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
 * Created by jonahelbaz on 2017-12-22.
 */

public class MembersAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<User> mMembers;

    // 1
    public MembersAdapter(Context context, List<User> members) {
        this.mContext = context;
        this.mMembers = members;
    }

    // 2
    @Override
    public int getCount() {
        return mMembers.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.member_cell, null);
        }
        final ImageView profilePicture = (ImageView)convertView.findViewById(R.id.ProfilePicture);
        final TextView name = (TextView)convertView.findViewById(R.id.Name);
        final TextView degree = (TextView)convertView.findViewById(R.id.Degree);

        Glide.with(mContext)
                .load(mMembers.get(position).Image)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);
        name.setText(mMembers.get(position).First_Name + " " + mMembers.get(position).Last_Name);
        degree.setText(mMembers.get(position).Degree);


        return convertView;
    }
}
