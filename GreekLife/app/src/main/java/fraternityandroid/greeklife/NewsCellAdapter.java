//package fraternityandroid.greeklife;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ListAdapter;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
///**
// * Created by Jon Zlotnik on 2017-12-21.
// */
//
//public class NewsCellAdapter extends BaseAdapter implements ListAdapter {
//
//    private ArrayList<String> list = new ArrayList<String>();
//    private Context context;
//
//    public NewsCellAdapter(ArrayList<String> list, Context context) {
//        this.list = list;
//        this.context = context;
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int pos) {
//        return list.get(pos);
//    }
//
//    @Override
//    public long getItemId(int pos) {
//        return 0;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.news_cell, null);
//        }
//
//        //Handle TextView and display string from your list
//        TextView tvContact= (TextView)view.findViewById(R.id.tvContact);
//        tvContact.setText(list.get(position));
//
//        //Handle buttons and add onClickListeners
//        Button callbtn= (Button)view.findViewById(R.id.btn);
//
//        callbtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//
//            }
//        });
//        addBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                notifyDataSetChanged();
//            .
//            }
//        });
//
//        return view;
//    }
//}
