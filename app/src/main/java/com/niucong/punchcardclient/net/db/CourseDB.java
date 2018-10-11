package com.niucong.punchcardclient.net.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * 课程表
 */
public class CourseDB extends LitePalSupport implements Parcelable {

    private long id;// 唯一主键

    private int memberId;//  人员Id
    private String courseName;// 课程名称
    private String teacherName;// 老师名称
    private String roomName;// 上课地点

    private long[] classTimeDBs;// 上课时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getRoomName() {
        return roomName;
    }

    public long[] getClassTimeDBs() {
        return classTimeDBs;
    }

    public void setClassTimeDBs(long[] classTimeDBs) {
        this.classTimeDBs = classTimeDBs;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.memberId);
        dest.writeString(this.courseName);
        dest.writeString(this.teacherName);
        dest.writeString(this.roomName);
        dest.writeLongArray(this.classTimeDBs);
    }

    public CourseDB() {
    }

    protected CourseDB(Parcel in) {
        this.id = in.readLong();
        this.memberId = in.readInt();
        this.courseName = in.readString();
        this.teacherName = in.readString();
        this.roomName = in.readString();
        this.classTimeDBs = in.createLongArray();
    }

    public static final Creator<CourseDB> CREATOR = new Creator<CourseDB>() {
        @Override
        public CourseDB createFromParcel(Parcel source) {
            return new CourseDB(source);
        }

        @Override
        public CourseDB[] newArray(int size) {
            return new CourseDB[size];
        }
    };
}
