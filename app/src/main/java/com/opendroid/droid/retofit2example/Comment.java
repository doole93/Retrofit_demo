package com.opendroid.droid.retofit2example;

/**
 * Created by duce on 20-Sep-16.
 */
public class Comment
{
    private String _id;
    private String content;
    private String toUser;

    public Comment() {}

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        String[] help= _id.split("_");
        return help[1]+":"+help[0]+content;
    }
}
