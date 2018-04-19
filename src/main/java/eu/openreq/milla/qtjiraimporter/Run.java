package eu.openreq.milla.qtjiraimporter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Run {

    public String run(String url, OkHttpClient client) throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            return response.body().string();
        }
    }
}