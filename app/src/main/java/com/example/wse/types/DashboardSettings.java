package com.example.wse.types;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.wse.BR;

public final class DashboardSettings extends BaseObservable implements Parcelable {
    private AccelerationUnit accelerationUnit;
    private Integer decimalPrecision;
    private DashboardTheme theme;
    private String userName;

    /**
     * Parcelable implementation
     */
    private Float sUnit;
    private Integer sPrecision;
    private String sTheme;
    private String sUserName;

    public static final Parcelable.Creator<DashboardSettings> CREATOR = new Parcelable.Creator<>() {

        public DashboardSettings createFromParcel(Parcel in) {
            return new DashboardSettings(in);
        }

        public DashboardSettings[] newArray(int size) {
            return new DashboardSettings[size];
        }
    };

    public DashboardSettings() {
        this(AccelerationUnit.KILOMETER_PER_HOUR, 2, DashboardTheme.LIGHT, "Anon");
    }

    private DashboardSettings(Parcel in) {
        sUnit = in.readFloat();
        sPrecision = in.readInt();
        sTheme = in.readString();
        sUserName = in.readString();

        accelerationUnit = AccelerationUnit.fromValue(sUnit);
        decimalPrecision = sPrecision;
        theme = DashboardTheme.fromValue(sTheme);
        userName = sUserName;
    }

    private DashboardSettings(AccelerationUnit accelerationUnit, Integer decimalPrecision, DashboardTheme theme, String userName) {
        this.accelerationUnit = accelerationUnit;
        this.decimalPrecision = decimalPrecision;
        this.theme = theme;
        this.userName = userName;

        /*
         * For "Parcelable implementation"
         */
        this.sUnit = accelerationUnit.getValue();
        this.sPrecision = decimalPrecision;
        this.sTheme = theme.getTheme();
        this.sUserName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcelOut, int flags) {
        parcelOut.writeFloat(sUnit);
        parcelOut.writeInt(sPrecision);
        parcelOut.writeString(sTheme);
        parcelOut.writeString(sUserName);
    }
    /**
     * END Parcelable implementation
     */

    public Integer getDecimalPrecision() {
        return decimalPrecision;
    }

    public void setDecimalPrecision(Integer decimalPrecision) {
        this.decimalPrecision = decimalPrecision;
        this.sPrecision = decimalPrecision;
    }

    @Bindable
    public DashboardTheme getTheme() {
        return theme;
    }

    public void setTheme(DashboardTheme theme) {
        this.theme = theme;
        this.sTheme = theme.getTheme();
        notifyPropertyChanged(BR.theme);
    }

    public AccelerationUnit getAccelerationUnit() {
        return accelerationUnit;
    }

    public void setAccelerationUnit(AccelerationUnit accelerationUnit) {
        this.accelerationUnit = accelerationUnit;
        this.sUnit = accelerationUnit.getValue();
        notifyPropertyChanged(BR.accelerationUnitString);
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.sUserName = userName;
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getAccelerationUnitString() {
        return accelerationUnit == AccelerationUnit.KILOMETER_PER_HOUR ? "km/h" : "m/s";
    }
}
