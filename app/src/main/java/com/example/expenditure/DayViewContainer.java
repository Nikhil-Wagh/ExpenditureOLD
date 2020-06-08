package com.example.expenditure;

import android.view.View;
import android.widget.TextView;

import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.ViewContainer;


public class DayViewContainer extends ViewContainer {

    private TextView dateTextView;
    private CalendarDay day;

    public DayViewContainer(View view) {
        super(view);
        dateTextView = view.findViewById(R.id.calendarDayText);
    }

    public void setDateTextView(int date) {
        this.dateTextView.setText(String.valueOf(date));
    }

    public CalendarDay getDay() {
        return day;
    }

    public void setDay(CalendarDay day) {
        this.day = day;
    }

    public void makeTextViewVisible() {
        this.dateTextView.setVisibility(View.VISIBLE);
    }

    public DayOwner getOwner() {
        return this.day.getOwner();
    }

    public void updateUI(int textColor) {
        dateTextView.setTextColor(textColor);
    }

    public void makeTextViewInVisible() {
        dateTextView.setVisibility(View.GONE);
    }

    public void setTextColor(int textColor) {
        dateTextView.setTextColor(textColor);
    }
}
