package com.mobile.CloudMovie.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

public interface IActivity {

    /**
     * 跳转activity
     */
    void goActivity(@NonNull Intent it);

    /**
     * 跳转activity
     */
    void goActivity(@NonNull Class<?> clazz);

    /**
     * 跳转activity
     */
    void goActivity(@NonNull Class<?> cls, @NonNull Bundle extras);

}
