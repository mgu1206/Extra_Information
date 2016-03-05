package yu_cse.graduation_project_edit.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by gyeunguckmin on 10/3/15.
 */
public class PhpExcute extends AsyncTask<String, Integer, String> {



    public String resultString = new String();


    @Override
    protected void onPreExecute() {


        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder jsonHtml = new StringBuilder();
        try {
            URL url = new URL(urls[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line = br.readLine();
                    jsonHtml.append(line);
                    br.close();
                }
                conn.disconnect();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "fail";
        }
        return jsonHtml.toString();
    }

    protected void onPostExecute(String str) {


        setResultString(str);
    }

    public void setResultString(String str)
    {
        resultString = str;
    }

    public String getResultString()
    {
        return resultString;
    }
}
