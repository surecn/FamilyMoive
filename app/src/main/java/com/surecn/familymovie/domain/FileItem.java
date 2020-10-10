package com.surecn.familymovie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-25
 * Time: 14:37
 */
public class FileItem implements Parcelable {
    public int canAccess;
    public int type;
    public String name;
    public String path;
    public String extension;
    public String lastModify;
    public String user;
    public String pass;
    public String server;
    public int needPass;
    public int custom;

    public int favoriteType;

    public int accessed;

    public int getFavoriteType() {
        return favoriteType;
    }

    public void setFavoriteType(int favoriteType) {
        this.favoriteType = favoriteType;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getCanAccess() {
        return canAccess;
    }

    public void setCanAccess(int canAccess) {
        this.canAccess = canAccess;
    }

    public int getNeedPass() {
        return needPass;
    }

    public void setNeedPass(int needPass) {
        this.needPass = needPass;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    public int getCustom() {
        return custom;
    }

    public void setCustom(int custom) {
        this.custom = custom;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof FileItem) {
            FileItem fileItem = (FileItem) obj;
            if (fileItem.type == type && (fileItem.path != null && fileItem.path.equals(path))) {
                return true;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.canAccess);
        dest.writeInt(this.type);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.extension);
        dest.writeString(this.lastModify);
        dest.writeString(this.user);
        dest.writeString(this.pass);
        dest.writeString(this.server);
        dest.writeInt(this.custom);
    }

    public FileItem() {
    }

    protected FileItem(Parcel in) {
        this.canAccess = in.readInt();
        this.type = in.readInt();
        this.name = in.readString();
        this.path = in.readString();
        this.extension = in.readString();
        this.lastModify = in.readString();
        this.user = in.readString();
        this.pass = in.readString();
        this.server = in.readString();
        this.custom = in.readInt();
    }

    public static final Parcelable.Creator<FileItem> CREATOR = new Parcelable.Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel source) {
            return new FileItem(source);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };
}
