package fraternityandroid.greeklife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jonahelbaz on 2017-12-24.
 */

public class HomeAdapter extends BaseAdapter {


    private final Context mContext;
    private List<String> mNews;

    // 1
    public HomeAdapter(Context context, List<String> news) {
        this.mContext = context;
        this.mNews = news;
    }

    // 2
    @Override
    public int getCount() {
        return mNews.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        Globals globals = Globals.getInstance();

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.home_new_cell, null);
        }

        TextView text = (TextView) convertView.findViewById(R.id.Cell);
        text.setText(mNews.get(position));


        //Set the order of the posts
        return convertView;
    }
}
