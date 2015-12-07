package ru.org.adons.clog;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public abstract class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements LogcatAdapter {

    public static final String ERROR_MESSAGE = "Unable to get LogCat";
    public static final String LOG_TAG = "CLOG.LOG_TAG";
    private static final ArrayList<Message> items = new ArrayList<>();
    // store all items before filtering
    private static final ArrayList<Message> allItems = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView header;
        public final TextView subHeader;
        /* header colors */
        public final int headerColorDefault;
        public final int headerColorRed;
        public final int headerColorOrange;
        public final int headerColorGreen;

        @SuppressWarnings("deprecation")
        public ViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(android.R.id.text1);
            subHeader = (TextView) view.findViewById(android.R.id.text2);
            /* header colors */
            headerColorDefault = header.getCurrentTextColor();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                headerColorRed = view.getContext().getResources().getColor(android.R.color.holo_red_dark, view.getContext().getTheme());
                headerColorOrange = view.getContext().getResources().getColor(android.R.color.holo_orange_dark, view.getContext().getTheme());
                headerColorGreen = view.getContext().getResources().getColor(android.R.color.holo_green_dark, view.getContext().getTheme());
            } else {
                headerColorRed = view.getContext().getResources().getColor(android.R.color.holo_red_dark);
                headerColorOrange = view.getContext().getResources().getColor(android.R.color.holo_orange_dark);
                headerColorGreen = view.getContext().getResources().getColor(android.R.color.holo_green_dark);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        view.setClickable(true);

        final TypedValue backgroundValue = new TypedValue();
        parent.getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, backgroundValue, true);
        view.setBackgroundResource(backgroundValue.resourceId);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Message message = items.get(position);
        holder.header.setText(message.getHeader());
        holder.subHeader.setText(message.getSubHeader());

        /* header colors */
        if (message.getLevel() == Message.Level.ERROR) {
            holder.header.setTextColor(holder.headerColorRed);
        } else if (message.getLevel() == Message.Level.WARN) {
            holder.header.setTextColor(holder.headerColorOrange);
        } else if (message.getLevel() == Message.Level.DEBUG) {
            holder.header.setTextColor(holder.headerColorGreen);
        } else {
            holder.header.setTextColor(holder.headerColorDefault);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullText = message.getHeader();
                if (!TextUtils.isEmpty(message.getSubHeader())) {
                    fullText += "\n\n" + message.getSubHeader();
                }
                if (!TextUtils.isEmpty(message.getBody())) {
                    fullText += "\n" + message.getBody();
                }
                onClickItem(fullText);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Asynchronously load LogCat messages into RecyclerView
     * <p>
     * Each item in List display message header with metadata and first row of raw message,
     * whole message returned in {@link #onClickItem(String logMessage)}
     * If load failed, return FALSE in {@link #onLoadFinished(boolean isLoadSuccess)}
     * </p>
     */
    @Override
    public void loadItems() {
        new MessagesLoader().execute();
    }

    /**
     * Callback method before loadItems, use to show Progress
     */
    @Override
    public abstract void onLoadStarted();

    /**
     * Callback method when loadItems finished, use to hide Progress and show Toast
     *
     * @param isLoadSuccess is true when {@link #loadItems()} was without errors
     */
    @Override
    public abstract void onLoadFinished(boolean isLoadSuccess);

    /**
     * Clear RecyclerView
     */
    @Override
    public void clearItems() {
        items.clear();
        allItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Handle Click on RecyclerView list item
     *
     * @param logMessage is whole LogCat message (-v long message format)
     */
    @Override
    public abstract void onClickItem(String logMessage);

    private final class MessagesLoader extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            allItems.clear();
            onLoadStarted();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Process process = Runtime.getRuntime().exec("logcat -d -v long");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                boolean isHeader = false;
                Message message = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!TextUtils.isEmpty(line)) {
                        if (line.startsWith("[") || line.startsWith("---------")) {
                            // add previous message
                            if (message != null) {
                                items.add(message);
                            }
                            message = new Message();
                            message.setHeader(line);
                            isHeader = true;
                            // add Sub Header
                        } else if (isHeader) {
                            message.setSubHeader(line);
                            isHeader = false;
                            // add Body
                        } else if (message != null) {
                            message.setBody(line);
                        }
                    }
                }
                // add last message
                if (message != null) {
                    items.add(message);
                }
                return true;
            } catch (IOException e) {
                Log.e(LOG_TAG, ERROR_MESSAGE, e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean res) {
            if (res) {
                notifyDataSetChanged();
            }
            onLoadFinished(res);
            super.onPostExecute(res);
        }
    }

    /**
     * Show only message with certain level
     *
     * @param level is constant {@see ru.org.adons.clog.Message.Level}
     *              <ul>
     *              <li>VERBOSE</li>
     *              <li>DEBUG</li>
     *              <li>INFO</li>
     *              <li>WARN</li>
     *              <li>ERROR</li>
     *              <li>ASSERT</li>
     *              <ul/>
     */
    @Override
    public void filterByLevel(Message.Level level) {
        if (allItems.size() == 0) {
            allItems.addAll(items);
        }
        if (level == null) {
            items.clear();
            items.addAll(allItems);
        } else {
            final ArrayList<Message> newItems = new ArrayList<>();
            for (Message m : allItems) {
                if (m.getLevel() == level) {
                    newItems.add(m);
                }
            }
            items.clear();
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }
}
