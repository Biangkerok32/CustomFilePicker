package com.wangsun.custompicker.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ChosenFile implements Parcelable {
    private long id;
    private String queryUri;
    /**
     * Processed path to file. This should always be a local path on the device.
     */
    private String originalPath;

    /**
     * Size of the file in bytes
     */
    private long size;

    private Date createdAt;
    /**
     * Type of the file (image, video, file, audio etc).
     * This is for internal use.
     */
    private String type;
    /**
     * Display name of the file
     */
    private int requestId;
    private String displayName;
    private boolean success;

    public ChosenFile() {

    }

    protected ChosenFile(Parcel in) {
        id = in.readLong();
        queryUri = in.readString();
        originalPath = in.readString();
        size = in.readLong();
        createdAt = new Date(in.readLong());
        type = in.readString();
        displayName = in.readString();
        success = in.readByte() != 0;
        directoryType = in.readString();
        requestId = in.readInt();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(queryUri);
        dest.writeString(originalPath);
        dest.writeLong(size);
        dest.writeLong(createdAt.getTime());
        dest.writeString(type);
        dest.writeString(displayName);
        dest.writeInt(success ? 1 : 0);
        dest.writeString(directoryType);
        dest.writeInt(requestId);
    }

    public static final Creator<ChosenFile> CREATOR = new Creator<ChosenFile>() {
        @Override
        public ChosenFile createFromParcel(Parcel in) {
            return new ChosenFile(in);
        }

        @Override
        public ChosenFile[] newArray(int size) {
            return new ChosenFile[size];
        }
    };

    /**
     * If this file has been successfully processed.
     *
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Display name of the file
     *
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String directoryType;

    /**
     * Internal use
     *
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     * Internal use
     *
     * @return
     */
    public String getDirectoryType() {
        return directoryType;
    }

    public void setDirectoryType(String directoryType) {
        this.directoryType = directoryType;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQueryUri() {
        return queryUri;
    }

    public void setQueryUri(String queryUri) {
        this.queryUri = queryUri;
    }

    /**
     * Path to the processed file. This will always be a local path on the device.
     *
     * @return
     */
    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }


    /**
     * Get the size of the processed file in bytes
     *
     * @return
     */
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * For internal use
     *
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    private final static String STRING_FORMAT = "Type: %s, QueryUri: %s, Original Path: %s, Size: %s";

    @Override
    public String toString() {
        return String.format(STRING_FORMAT, type, queryUri, originalPath, getHumanReadableSize(false));
    }

    /**
     * Get File size in a pretty format.
     *
     * @param si
     * @return
     */
    public String getHumanReadableSize(boolean si) {
        int unit = si ? 1000 : 1024;
        if (size < unit) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + "";
        return String.format(Locale.ENGLISH, "%.1f %sB", size / Math.pow(unit, exp), pre);
    }

    /**
     * Get Duration (for audio and video) in a pretty format
     *
     * @param duration
     * @return
     */
    public String getHumanReadableDuration(long duration) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public boolean equals(Object obj) {
        ChosenFile other = (ChosenFile)obj;
        String otherString = getIdString(other);
        String thisString = getIdString(this);
        return otherString.equals(thisString);
    }

    @Override
    public int hashCode() {
        return getIdString(this).hashCode();
    }

    private String getIdString(ChosenFile file){
        String id = queryUri + ":" + originalPath + ":" +size;
        return id;
    }
}
