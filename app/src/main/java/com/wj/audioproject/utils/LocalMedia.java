package com.wj.audioproject.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class LocalMedia implements Parcelable {
    private long id;
    private String path;
    private String realPath;
    private String originalPath;
    private String compressPath;
    private String cutPath;
    private String androidQToPath;
    private String filePath;
    private long duration;
    private boolean isChecked;
    private boolean isCut;
    public int position;
    private int num;
    private String mimeType;
    private int chooseModel;
    private boolean compressed;
    private int width;
    private int height;
    private long size;
    private boolean isOriginal;
    private String fileName;
    private String parentFolderName;
    private int orientation = -1;
    public int loadLongImageStatus = -1;
    public boolean isLongImage;
    private long bucketId = -1L;
    private boolean isMaxSelectEnabledMask;
    public static final Creator<LocalMedia> CREATOR = new Creator<LocalMedia>() {
        public LocalMedia createFromParcel(Parcel source) {
            return new LocalMedia(source);
        }

        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }
    };

    public LocalMedia() {
    }

    public LocalMedia(String path, long duration, int chooseModel, String mimeType) {
        this.path = path;
        this.duration = duration;
        this.chooseModel = chooseModel;
        this.mimeType = mimeType;
    }

    public LocalMedia(long id, String path, String fileName, String parentFolderName, long duration, int chooseModel, String mimeType, int width, int height, long size) {
        this.id = id;
        this.path = path;
        this.fileName = fileName;
        this.parentFolderName = parentFolderName;
        this.duration = duration;
        this.chooseModel = chooseModel;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    public LocalMedia(long id, String path, String absolutePath, String fileName, String parentFolderName, long duration, int chooseModel, String mimeType, int width, int height, long size, long bucketId) {
        this.id = id;
        this.path = path;
        this.realPath = absolutePath;
        this.fileName = fileName;
        this.parentFolderName = parentFolderName;
        this.duration = duration;
        this.chooseModel = chooseModel;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.size = size;
        this.bucketId = bucketId;
    }

    public LocalMedia(String path, long duration, boolean isChecked, int position, int num, int chooseModel) {
        this.path = path;
        this.duration = duration;
        this.isChecked = isChecked;
        this.position = position;
        this.num = num;
        this.chooseModel = chooseModel;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCompressPath() {
        return this.compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getCutPath() {
        return this.cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public String getAndroidQToPath() {
        return this.androidQToPath;
    }

    public void setAndroidQToPath(String androidQToPath) {
        this.androidQToPath = androidQToPath;
    }

    public String getFilePath() {
        return PictureMimeType.isContent(this.path) ? this.realPath : this.path;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getRealPath() {
        return this.realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    public boolean isCut() {
        return this.isCut;
    }

    public void setCut(boolean cut) {
        this.isCut = cut;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMimeType() {
        return TextUtils.isEmpty(this.mimeType) ? "image/jpeg" : this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getChooseModel() {
        return this.chooseModel;
    }

    public void setChooseModel(int chooseModel) {
        this.chooseModel = chooseModel;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isOriginal() {
        return this.isOriginal;
    }

    public void setOriginal(boolean original) {
        this.isOriginal = original;
    }

    public String getOriginalPath() {
        return this.originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParentFolderName() {
        return this.parentFolderName;
    }

    public void setParentFolderName(String parentFolderName) {
        this.parentFolderName = parentFolderName;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public long getBucketId() {
        return this.bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public boolean isMaxSelectEnabledMask() {
        return this.isMaxSelectEnabledMask;
    }

    public void setMaxSelectEnabledMask(boolean maxSelectEnabledMask) {
        this.isMaxSelectEnabledMask = maxSelectEnabledMask;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.path);
        dest.writeString(this.realPath);
        dest.writeString(this.originalPath);
        dest.writeString(this.compressPath);
        dest.writeString(this.cutPath);
        dest.writeString(this.androidQToPath);
        dest.writeString(this.filePath);
        dest.writeLong(this.duration);
        dest.writeByte((byte)(this.isChecked ? 1 : 0));
        dest.writeByte((byte)(this.isCut ? 1 : 0));
        dest.writeInt(this.position);
        dest.writeInt(this.num);
        dest.writeString(this.mimeType);
        dest.writeInt(this.chooseModel);
        dest.writeByte((byte)(this.compressed ? 1 : 0));
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.size);
        dest.writeByte((byte)(this.isOriginal ? 1 : 0));
        dest.writeString(this.fileName);
        dest.writeString(this.parentFolderName);
        dest.writeInt(this.orientation);
        dest.writeInt(this.loadLongImageStatus);
        dest.writeByte((byte)(this.isLongImage ? 1 : 0));
        dest.writeLong(this.bucketId);
        dest.writeByte((byte)(this.isMaxSelectEnabledMask ? 1 : 0));
    }

    protected LocalMedia(Parcel in) {
        this.id = in.readLong();
        this.path = in.readString();
        this.realPath = in.readString();
        this.originalPath = in.readString();
        this.compressPath = in.readString();
        this.cutPath = in.readString();
        this.androidQToPath = in.readString();
        this.filePath = in.readString();
        this.duration = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.isCut = in.readByte() != 0;
        this.position = in.readInt();
        this.num = in.readInt();
        this.mimeType = in.readString();
        this.chooseModel = in.readInt();
        this.compressed = in.readByte() != 0;
        this.width = in.readInt();
        this.height = in.readInt();
        this.size = in.readLong();
        this.isOriginal = in.readByte() != 0;
        this.fileName = in.readString();
        this.parentFolderName = in.readString();
        this.orientation = in.readInt();
        this.loadLongImageStatus = in.readInt();
        this.isLongImage = in.readByte() != 0;
        this.bucketId = in.readLong();
        this.isMaxSelectEnabledMask = in.readByte() != 0;
    }
}
