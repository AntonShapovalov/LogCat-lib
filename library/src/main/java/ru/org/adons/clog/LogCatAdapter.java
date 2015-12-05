package ru.org.adons.clog;

public interface LogCatAdapter {
    void loadItems();
    void onLoadStarted();
    void onLoadFinished(boolean isLoadSuccess);
    void clearItems();
    void onClickItem(String logMessage);
}
