package com.niucong.punchcardclient.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.niucong.punchcardclient.net.db.ClassTimeDB;

import java.util.HashMap;
import java.util.Map;

public class ParcelableMap implements Parcelable {

    private Map<Integer, ClassTimeDB> map;

    public Map<Integer, ClassTimeDB> getMap() {
        return map;
    }

    public void setMap(Map<Integer, ClassTimeDB> map) {
        this.map = map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.map.size());
        for (Map.Entry<Integer, ClassTimeDB> entry : this.map.entrySet()) {
            dest.writeValue(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    public ParcelableMap() {
    }

    protected ParcelableMap(Parcel in) {
        int mapSize = in.readInt();
        this.map = new HashMap<Integer, ClassTimeDB>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            Integer key = (Integer) in.readValue(Integer.class.getClassLoader());
            ClassTimeDB value = in.readParcelable(ClassTimeDB.class.getClassLoader());
            this.map.put(key, value);
        }
    }

    public static final Creator<ParcelableMap> CREATOR = new Creator<ParcelableMap>() {
        @Override
        public ParcelableMap createFromParcel(Parcel source) {
            return new ParcelableMap(source);
        }

        @Override
        public ParcelableMap[] newArray(int size) {
            return new ParcelableMap[size];
        }
    };
}
