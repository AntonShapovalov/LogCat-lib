package ru.org.adons.clog;

import android.text.TextUtils;

public class Message {
    public enum Level {VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT}

    private String header;
    private String subHeader;
    private String body;
    private Level level;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
        if (header.contains("V/")) {
            level = Level.VERBOSE;
        } else if (header.contains("D/")) {
            level = Level.DEBUG;
        } else if (header.contains("I/")) {
            level = Level.INFO;
        } else if (header.contains("W/")) {
            level = Level.WARN;
        } else if (header.contains("E/")) {
            level = Level.ERROR;
        } else if (header.contains("A/")) {
            level = Level.ASSERT;
        }
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        if (!TextUtils.isEmpty(this.body)) {
            this.body += "\n" + body;
        } else {
            this.body = body;
        }
    }

    public Level getLevel() {
        return this.level;
    }

}
