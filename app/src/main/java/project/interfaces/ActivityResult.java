package project.interfaces;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface ActivityResult {
    void onGranted(int requestCode, int resultCode, @Nullable Intent data);
}
