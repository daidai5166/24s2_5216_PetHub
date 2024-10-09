package comp5216.sydney.edu.au.pethub.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MarshmallowPermission {

    public static final int RECORD_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int READFILES_PERMISSION_REQUEST_CODE = 4;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 5;
    Activity activity;

    public MarshmallowPermission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionForRecord() {
        int result = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.RECORD_AUDIO
        );
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForExternalStorage() {
        int result = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
        Log.d("Permission", "checkPermissionForExternalStorage: " + result);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        Log.d("Permission", "checkPermissionForCamera: " + result);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForLocation() {
        int result = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
        );
        Log.i("Permission", "checkPermissionForLocation: " + result);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionForLocation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
        )) {
            Toast.makeText(activity,
                    "Microphone permission needed for location. " +
                            "Please allow in App Settings for additional functionality.",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.CAMERA
        )) {
            Toast.makeText(activity,
                    "Camera permission needed. " +
                            "Please allow in App Settings for additional functionality.",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, CAMERA_PERMISSION_REQUEST_CODE);
            System.out.print("get camera permission");
        }
    }
}
