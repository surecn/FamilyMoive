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
