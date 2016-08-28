package xyz.mhuy.noteit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteListAdapter extends BaseAdapter {
    private Activity mContext;
    private List<Note> mList;

    public NoteListAdapter(Activity paramActivity, List<Note> paramList)
    {
        this.mContext = paramActivity;
        this.mList = paramList;
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        Holder holder;
        if (view == null) {
            v = ((LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.note_row, null);
            holder = new Holder(v);
            v.setTag(holder);
        } else {
            holder = (Holder)v.getTag();
        }

        Note n = (Note) getItem(i);

        String temp = n.getTitle();
        if(temp.length() > 20) {
            holder.tvTitle.setText(temp.substring(0, 19) + "...");
        } else {
            holder.tvTitle.setText(temp);
        }

        String dateString = new SimpleDateFormat("HH:mm MM-dd-yyyy").format(new Date(n.getDate()));
        holder.tvDate.setText(dateString);

        return v;
    }


    static class Holder{
        protected TextView tvTitle;
        protected TextView tvDate;

        public Holder(View paramView) {
            this.tvTitle = ((TextView)paramView.findViewById(R.id.tv_title));
            this.tvDate = ((TextView)paramView.findViewById(R.id.tv_date));
        }
    }
}
