package eu.openreq.milla.qtjiraimporter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Run {
//	testiesti
    public String run(String url, OkHttpClient client) throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute())
        {	
//        	if (!response.isSuccessful()) {
//        		System.out.println(response);
//        		//throw new IOException("Unexpected code " + response);
//        		return null;
//        	}
        	String result = response.body().string(); //Changed this, added close...

        //	response.close(); //This is here for running QTBUG in four parts
            return result;
        //	return response.body().string();
        }
//        catch(Exception e) {
//        	System.out.println(e);
//        	return null;
//        }
    }
}