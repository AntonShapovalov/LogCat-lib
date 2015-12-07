package ru.org.adons.clog;

public interface LogcatAdapter {
    void loadItems();
    void onLoadStarted();
    void onLoadFinished(boolean isLoadSuccess);
    void clearItems();
    void onClickItem(String logMessage);
    void filterByLevel(Message.Level level);
}
