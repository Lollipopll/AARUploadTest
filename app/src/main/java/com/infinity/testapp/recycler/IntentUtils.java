package com.infinity.testapp.recycler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author wang
 * @date 2020/9/21
 * des
 */
public class IntentUtils {

    public static void openToUri(Context context, String uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri contentUrl = Uri.parse(uri);
        intent.setData(contentUrl);
        context.startActivity(intent);

    }
}
