package es.upm.miw.helpifororganizations.utils;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.util.Patterns;

import com.google.android.material.datepicker.CalendarConstraints;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.upm.miw.helpifororganizations.models.Location;

public class Validators {

    public static String validateEmail(String email) {
        if (email.isEmpty())
            return "Email can not be empty";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Invalid email address";
        return null;
    }

    public static String validatePassword(String password) {
        if (password.isEmpty())
            return "Password can not be empty";
        if (password.length() < 8)
            return "Minimum 8 characters";
        return null;
    }

    public static String validateName(String name) {
        if (name.isEmpty())
            return "Name can not be empty";
        if (name.length() < 8)
            return "Minimum 8 characters";
        if (name.length() > 60)
            return "Maximum 60 characters";
        return null;
    }

    public static String validateLocation(Location location) {
        if (location == null)
            return "Location can not be empty";
        if (location.getPlace().isEmpty())
            return "Location place can not be empty";
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String validateDateTime(String dateTime) {
        if (dateTime.isEmpty()) {
            return "Date time can not be empty";
        }
        try {
            Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateTime);
            if (new Date().getTime() > parse.getTime()) {
                return "Date and time must be in the future";
            }
        } catch (ParseException e) {
            return "Date time format is incorrect. It must be yyyy-MM-dd HH:mm";
        }
        return null;
    }

    public static String validateMaxParticipants(String editable) {
        if (editable.isEmpty()) {
            return "Number of participants can not be empty";
        }
        int parseInt = Integer.parseInt(editable);
        if (parseInt < 0)
            return "Number of participants must be positive";
        if (parseInt >= 1000)
            return "Number of participants must be lower than 1000";
        return null;
    }

    public static String validateDescription(String description) {
        if (description.isEmpty())
            return "Description can not be empty";
        if (description.length() < 8)
            return "Description must be greater than 8 characters";
        if (description.length() >= 300)
            return "Description must be lower than 300 characters";
        return null;
    }

    public static String validateBody(String body) {
        if (body.isEmpty())
            return "Body can not be empty";
        if (body.length() < 8)
            return "Body must be greater than 8 characters";
        if (body.length() >= 1000)
            return "Body must be lower than 1000 characters";
        return null;
    }
}
