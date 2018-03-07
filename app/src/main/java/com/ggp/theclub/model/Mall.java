package com.ggp.theclub.model;

import android.location.Location;

import com.ggp.theclub.util.StringUtils;
import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import static com.ggp.theclub.util.DateUtils.nullsafeConvertToLocalDate;

public class Mall implements Comparable<Mall> {
    private static int INVALID_MALL_ID = -1;

    public enum MallStatus { ACTIVE, LEGACY_PLATFORM, DISPOSITIONING, DISPOSITIONED }

    @Getter @Setter private int id;
    @Getter @Setter private String name;
    @Getter @Setter private Address address;
    @Getter @Setter private double distanceFromSearchLocation;
    @Getter @Setter private double latitude;
    @Getter @Setter private double longitude;
    @Getter @Setter private String phoneNumber;
    @Getter @Setter private SocialMedia socialMedia;
    @Getter @Setter private String websiteUrl;
    @Setter private boolean hasTheater;
    @Getter @Setter private String nonSvgLogoUrl;
    @Getter @Setter private String inverseNonSvgLogoUrl;
    @Getter private String parkingDescription;
    @Getter @SerializedName("config") private MallConfig mallConfig;
    @Getter @Setter private Set<OperatingHours> operatingHours;
    @Getter @Setter private Set<OperatingHoursException> operatingHoursExceptions;
    @Getter @Setter private MallStatus status = MallStatus.ACTIVE;
    @Getter String timeZone;

    @Setter Date holidayHoursDisplayDate;
    @Setter Date holidayHoursStartDate;
    @Setter Date holidayHoursEndDate;
    @Setter Date blackFridayHoursDisplayDate;
    @Setter Date blackFridayHoursEndDate;
    @Setter String blackFridayHoursUrlPath;

    public Mall() {
        setId(INVALID_MALL_ID);
    }

    public int compareTo(Mall compareMall) {
        int theirDistance = (int) compareMall.getDistanceFromSearchLocation();
        int myDistance = (int) this.getDistanceFromSearchLocation();

        //ascending order
        return myDistance - theirDistance;
    }

    public boolean hasTheater() {
        return hasTheater;
    }

    public boolean isValid() {
        return getId() != INVALID_MALL_ID && getMallConfig() != null;
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public class SocialMedia {
        @Getter @Setter private String facebookUrl;
        @Getter @Setter private String instagramUrl;
        @Getter @Setter private String googlePlusUrl;
        @Getter @Setter private String twitterUrl;
        @Getter @Setter private String pinterestUrl;
    }

    public LocalDate getHolidayHoursDisplayDate() {
        return nullsafeConvertToLocalDate(holidayHoursDisplayDate);
    }

    public LocalDate getHolidayHoursStartDate() {
        return nullsafeConvertToLocalDate(holidayHoursStartDate);
    }


    public LocalDate getHolidayHoursEndDate() {
        return nullsafeConvertToLocalDate(holidayHoursEndDate);
    }

    public LocalDate getBlackFridayHoursDisplayDate() {
        return nullsafeConvertToLocalDate(blackFridayHoursDisplayDate);
    }

    public LocalDate getBlackFridayHoursEndDate() {
        return nullsafeConvertToLocalDate(blackFridayHoursEndDate);
    }

    public String getBlackFridayHoursUrl() {
        return StringUtils.isEmpty(blackFridayHoursUrlPath) || StringUtils.isEmpty(websiteUrl)
                ? null : websiteUrl + blackFridayHoursUrlPath;
    }
}