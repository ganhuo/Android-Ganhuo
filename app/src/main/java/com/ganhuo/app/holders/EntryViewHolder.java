package com.ganhuo.app.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ganhuo.app.R;
import com.ganhuo.app.WebViewActivity;
import com.ganhuo.app.models.Entry;

public class EntryViewHolder extends RecyclerView.ViewHolder {
    public final ImageView thumb;
    public final TextView title, content, time;
    public final View parent;

    private EntryViewHolder(View itemView) {
        super(itemView);
        thumb = (ImageView) itemView.findViewById(R.id.thumb);
        title = (TextView) itemView.findViewById(R.id.title);
        content = (TextView) itemView.findViewById(R.id.content);
        time = (TextView) itemView.findViewById(R.id.time);
        parent = itemView;
    }

    public static EntryViewHolder create(final Context context, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.entry_item, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Entry entry = (Entry) v.getTag();
                if (entry != null) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", entry.getUrl());
                    context.startActivity(intent);
                }
            }
        });
        return new EntryViewHolder(v);
    }

    public void onBindViewHolder(Entry entry) {
        parent.setTag(entry);
        title.setText(entry.getTitle());
        content.setText(entry.getContent());
        time.setText(entry.getBeautyPublishDate());
        Glide.with(thumb.getContext()).load(entry.getThumbUrl()).into(thumb);
    }
}
