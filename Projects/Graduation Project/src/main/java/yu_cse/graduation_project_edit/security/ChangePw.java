package yu_cse.graduation_project_edit.security;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gyeunguckmin on 8/20/15.
 */
public class ChangePw {

    PasswordChangeTask passwordChangeTask;

    public void changeLostPass()
    {
        passwordChangeTask = new PasswordChangeTask();
        passwordChangeTask.execute();
    }

    public void changeCommonPass()
    {
        passwordChangeTask = new PasswordChangeTask();
        passwordChangeTask.execute();
    }



    public class PasswordChangeTask extends AsyncTask<String, Integer,String> {

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
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {

        }
    }
}
