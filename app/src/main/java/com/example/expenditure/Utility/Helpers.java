package com.example.expenditure.Utility;

import com.google.firebase.Timestamp;

import org.threeten.bp.LocalDate;

import java.text.SimpleDateFormat;

public class Helpers {
    public static String localDateToStringDate(LocalDate date) {
        Timestamp timestamp = Firebase.getTimestamp(date);
        SimpleDateFormat sfd = new SimpleDateFormat("MMM dd, yyyy");
        return sfd.format(timestamp.toDate());
    }
}
