package com.bluewhaleyt.chatgpt.utils;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class CustomTypefaceSpan extends MetricAffectingSpan {
    private Typeface mTypeface;

    public CustomTypefaceSpan(Typeface typeface) {
        mTypeface = typeface;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);
    }
}