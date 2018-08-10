package knickknacker.assets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class AssetReader {
    public static String readString(Context context, String filename) throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    public static byte[] readBytes(Context context, String filename) throws IOException {
        InputStream is = context.getAssets().open(filename);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}
