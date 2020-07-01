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
    public boolean canAccess;
    public int type;
    public String name;
    public String path;
    public String extension;
    public String lastModify;
    public String user;
    public String pass;
    public String server;
    public boolean needPass;

    public int favoriteType;

    public int getFavoriteType() {
        return favoriteType;
    }

    public void setFavoriteType(int favoriteType) {
        this.favoriteType = favoriteType;
    }

    public boolean isNeedPass() {
        return needPass;
    }

    public void setNeedPass(boolean needPass) {
        this.needPass = needPass;
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

    public boolean isCanAccess() {
        return canAccess;
    }

    public void setCanAccess(boolean canAccess) {
        this.canAccess = canAccess;
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
        dest.writeByte(this.canAccess ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.extension);
        dest.writeString(this.lastModify);
        dest.writeString(this.user);
        dest.writeString(this.pass);
        dest.writeString(this.server);
    }

    public FileItem() {
    }

    protected FileItem(Parcel in) {
        this.canAccess = in.readByte() != 0;
        this.type = in.readInt();
        this.name = in.readString();
        this.path = in.readString();
        this.extension = in.readString();
        this.lastModify = in.readString();
        this.user = in.readString();
        this.pass = in.readString();
        this.server = in.readString();
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
