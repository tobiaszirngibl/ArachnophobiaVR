package com.example.tobias.arachnophobiavr;

import android.support.annotation.LayoutRes;

public enum FullscreenExample {
    REALTIME_SCROLLING(R.layout.fullscreen_example_simple, RealtimeScrolling.class);

    public static final String ARG_ID = "FEID";

    public final @LayoutRes
    int contentView;
    public final Class<? extends BaseExample> exampleClass;

    FullscreenExample(@LayoutRes int contentView, Class<? extends BaseExample> exampleClass) {
        this.contentView = contentView;
        this.exampleClass = exampleClass;
    }
}
