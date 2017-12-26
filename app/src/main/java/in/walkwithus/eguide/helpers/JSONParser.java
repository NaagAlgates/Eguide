package in.walkwithus.eguide.helpers;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */


public class JSONParser {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String TAG = JSONParser.class.getSimpleName();
    private OkHttpClient client = new OkHttpClient();
    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET method

    public String makeHttpRequest(String url, String jsonReqString) throws IOException {
        String jsonString;
        Request request;
        if(jsonReqString!=null) {
            RequestBody body = RequestBody.create(JSON, jsonReqString);
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(url)
                    .build();
        }
        Response response = client.newCall(request).execute();
        int statusCode = response.code();
        Logger.d(TAG, "Call to " + url + " returned response code " + statusCode);
        jsonString = response.message();
        if (statusCode != 200) {
            Logger.e(TAG, "Call to " + url + " failed with status " + statusCode);
            Logger.e(TAG, "Request: " + jsonReqString);
            Logger.e(TAG, "Response: " + jsonString);
        } else {
            Logger.i(TAG, "Call to " + url + " returned OK");
            Logger.d(TAG, "Request: " + jsonReqString);
            Logger.d(TAG, "Response: " + jsonString);
        }
        return response.body().string();
    }
}
