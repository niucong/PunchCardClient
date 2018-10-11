package com.niucong.punchcardclient.net.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * 上课时间表
 */
public class ClassTimeDB extends LitePalSupport implements Parcelable {

    private long id;// 唯一主键

    private String weekDay;// 周几
    private String sectionName;// 节次名称

    private long courseId;// 课程ID

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.weekDay);
        dest.writeString(this.sectionName);
        dest.writeLong(this.courseId);
    }

    public ClassTimeDB() {
    }

    protected ClassTimeDB(Parcel in) {
        this.id = in.readLong();
        this.weekDay = in.readString();
        this.sectionName = in.readString();
        this.courseId = in.readLong();
    }

    public static final Creator<ClassTimeDB> CREATOR = new Creator<ClassTimeDB>() {
        @Override
        public ClassTimeDB createFromParcel(Parcel source) {
            return new ClassTimeDB(source);
        }

        @Override
        public ClassTimeDB[] newArray(int size) {
            return new ClassTimeDB[size];
        }
    };
}
