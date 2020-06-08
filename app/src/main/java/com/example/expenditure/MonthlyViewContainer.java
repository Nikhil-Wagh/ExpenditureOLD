package com.example.expenditure;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kizitonwose.calendarview.ui.ViewContainer;

public class MonthlyViewContainer extends ViewContainer {

    public ConstraintLayout legendLayout;

    public MonthlyViewContainer(View view) {
        super(view);
        this.legendLayout = view.findViewById(R.id.legendLayout);
    }
}
