package ru.org.adons.clog;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final ArrayList<String> items = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView text;

        public ViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(android.R.id.text1);
        }
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        view.setClickable(true);

        final TypedValue backgroundValue = new TypedValue();
        parent.getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, backgroundValue, true);
        view.setBackgroundResource(backgroundValue.resourceId);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean loadItems() {
        items.clear();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                items.add(line);
            }
        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "Unable to get LogCat", e);
            return false;
        }
        return true;
    }

    public void clearItems() {
        items.clear();
    }

}
