package zenithinvo.vagishdixit.apiresponse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Vagish Dixit on 2/21/2018.
 */

public class ApiResponse {
    private Context context;
    private ResponseListener responseListener;
    private HashMap<String, String> headerParams;
    private Boolean jsonParsing;
    private Type classType;

    public ApiResponse(Context context, ResponseListener responseListener) {
        this.context = context;
        this.responseListener = responseListener;

    }


    public static ApiResponse with(Context context, ResponseListener responseListener) {
        return new ApiResponse(context, responseListener);
    }


    public void getRequestParams(String TAG, String url, HashMap<String, String> params) {
        final Get get = new Get(TAG, url, params);

        if (!isConnected()) {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        Thread getThread = new Thread(new Runnable() {
            @Override
            public void run() {
                get.execute();
            }
        });
        getThread.start();
    }

    public void getRequest(String TAG, String Url) {
        final Get get = new Get(TAG, Url);
        if (!isConnected()) {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        final Thread getThread = new Thread(new Runnable() {
            @Override
            public void run() {
                get.execute();
            }
        });
        getThread.start();
    }

    public void postRequest(String TAG, String url) {
        final Post post = new Post(TAG, url);

        if (!isConnected()) {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        Thread postThread = new Thread(new Runnable() {
            @Override
            public void run() {
                post.execute();
            }
        });
        postThread.start();
    }

    public void postRequestParams(String TAG, String url, HashMap<String, String> params) {
        final Post post = new Post(TAG, url, params);

        if (!isConnected()) {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        Thread postThread = new Thread(new Runnable() {
            @Override
            public void run() {
                post.execute();
            }
        });
        postThread.start();
    }

    public void postFile(String TAG, String url, HashMap<String, File> fileHashMap, HashMap<String, String> params) {
        final PostFile postFile = new PostFile(TAG, url, fileHashMap, params);
        if (!isConnected()) {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        Thread postFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                postFile.execute();
            }
        });
        postFileThread.start();
    }

    class Post extends AsyncTask<Void, Void, String> {
        String TAG;
        String Url;
        HashMap<String, String> params;
        private int errorResult = 0;

        Post(String tag, String url) {
            this.TAG = tag;
            this.Url = url;
        }

        Post(String tag, String url, HashMap<String, String> params) {
            this.TAG = tag;
            this.Url = url;
            this.params = params;
        }

        @Override
        protected void onPostExecute(String postResult) {
            super.onPostExecute(postResult);
            Log.d("----post result", postResult);

            if (errorResult == 0) {
                Log.d("---Result Call---", postResult);
                if (jsonParsing != null && jsonParsing) {
                    performParsing(TAG, postResult, classType);
                } else {
                    responseListener.stringResponse(TAG, postResult);
                }
            } else {
                Log.d("---Result Call---", "negative response");
                responseListener.negativeResponse(TAG, postResult);
                Toast.makeText(context, "Cannot get Response!!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            String responseStr = "";
            HashMap<String, String> emptyPair = new HashMap<>();
            emptyPair.put("", "");

            URL url = null;
            try {
                url = new URL(Url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (headerParams != null) {
                    applyHeaders(httpURLConnection);
                }
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream senderStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(senderStream, "UTF-8"));


                if (this.params == null)
                    writer.write(mapToString(emptyPair));
                else
                    writer.write(mapToString(this.params));

                writer.flush();
                writer.close();
                senderStream.close();

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseStr += line;
                    }
                } else {
                    responseStr = "Reponse Code : " + responseCode + "(" + httpURLConnection.getResponseMessage() + ")";
                    errorResult = 1;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                errorResult = 1;
                responseStr = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                errorResult = 1;
                responseStr = e.getMessage();
            }

            Log.d("----post response", responseStr);
            headerParams = null;
            return responseStr;
        }
    }

    class Get extends AsyncTask<Void, Void, String> {
        String TAG;
        String Url;
        HashMap<String, String> params;
        int errorResult = 0;

        Get(String tag, String Url) {
            this.TAG = tag;
            this.Url = Url;
        }

        Get(String tag, String Url, HashMap<String, String> params) {
            this.TAG = tag;
            this.Url = Url;
            this.params = params;
        }

        @Override
        protected void onPostExecute(String postResult) {
            super.onPostExecute(postResult);
            Log.d("----post result", postResult);

            if (errorResult == 0) {
                Log.d("---Result Call---", postResult);
                if (jsonParsing != null && jsonParsing) {
                    performParsing(TAG, postResult, classType);
                } else {
                    responseListener.stringResponse(TAG, postResult);
                }
            } else {
                Log.d("---Result Call---", "negative response");
                responseListener.negativeResponse(TAG, postResult);
                Toast.makeText(context, "Cannot get Response!!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            String responseStr = "";

            URL url = null;
            try {
                if (this.params != null)
                    Url += mapToString(this.params);
                url = new URL(Url);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (headerParams != null) {
                    applyHeaders(urlConnection);
                }
                urlConnection.setRequestMethod("GET");


                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseStr += line;
                    }
                } else {
                    responseStr = "Reponse Code : " + responseCode + "(" + urlConnection.getResponseMessage() + ")";
                    errorResult = 1;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                errorResult = 1;
                responseStr = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                errorResult = 1;
                responseStr = e.getMessage();
            }

            Log.d("----response", responseStr);
            headerParams = null;
            return responseStr;
        }
    }

    class PostFile extends AsyncTask<Void, Void, String> {

        String TAG, Url;
        HashMap<String, File> fileParams;
        HashMap<String, String> params;
        private int errorFlag = 0;

        PostFile(String tag, String url, HashMap<String, File> fileHashMap, HashMap<String, String> params) {
            this.TAG = tag;
            this.Url = url;
            this.fileParams = fileHashMap;
            this.params = params;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("----post result", result);

            if (errorFlag == 0) {
                Log.d("----Call : ", "positive");
                if (jsonParsing != null && jsonParsing)
                    performParsing(TAG, result, classType);
                else
                    responseListener.stringResponse(TAG, result);
            } else {
                Log.d("----Call : ", "negative");
                responseListener.negativeResponse(TAG, result);
            }

        }

        @Override
        protected String doInBackground(Void... voids) {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            HashMap<String, String> emptyValue = new HashMap<String, String>(2);
            emptyValue.put("", "");

            String contentDisposition = "Content-Disposition: form-data;";
            String contentTypeFile = "Content-Type: application/octet-stream";
            String responseStr = "";


            URL url = null;
            try {
                url = new URL(Url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                if (fileParams.size() == 0) {
                    responseStr = "No File Provided";
                    errorFlag = 1;
                    return "";
                }

                BufferedOutputStream fileWritter = new BufferedOutputStream(conn.getOutputStream());
                fileWritter.flush();
                StringBuffer requestString = new StringBuffer();
                for (Map.Entry<String, File> fileParam : fileParams.entrySet()) {
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write((twoHyphens + boundary + lineEnd).getBytes());
                    fileWritter.write(contentDisposition.getBytes());
                    fileWritter.write(String.format("name=%s;", fileParam.getKey()).getBytes());
                    fileWritter.write(String.format("filename='%s';", fileParam.getValue().getName()).getBytes());
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write(contentTypeFile.getBytes());
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write(lineEnd.getBytes());

                    FileInputStream fileStream = new FileInputStream(fileParam.getValue());

                    byte[] buff = new byte[1024];
                    int buffSize = 0;


                    while ((buffSize = fileStream.read(buff)) != -1) {
                        fileWritter.write(buff, 0, buffSize);
                        fileWritter.flush();
                    }

                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write((twoHyphens + boundary).getBytes());
                }

                for (Map.Entry<String, String> param : this.params.entrySet()) {
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write(contentDisposition.getBytes());
                    fileWritter.write(String.format("name=%s", param.getKey()).getBytes());
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write(param.getValue().getBytes());
                    fileWritter.write(lineEnd.getBytes());
                    fileWritter.write((twoHyphens + boundary).getBytes());
                }
                fileWritter.write((twoHyphens + lineEnd).getBytes());

                Log.d("----File Request", requestString.toString());

                fileWritter.flush();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String responseLine;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((responseLine = bufferedReader.readLine()) != null) {
                        responseStr += responseLine;
                    }
                } else {
                    responseStr = "Reponse Code : " + responseCode + "(" + conn.getResponseMessage() + ")";
                    errorFlag = 1;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            headerParams = null;
            return responseStr;
        }
    }


    public Boolean getJsonParsing() {
        return jsonParsing;
    }

    public void performJsonParsing(Boolean jsonParsing) {
        this.jsonParsing = jsonParsing;
    }

    public Type getClassType() {
        return classType;
    }

    public void setClassType(Type classType) {
        this.classType = classType;
    }

    private static String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            String value = map.get(key);
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return stringBuilder.toString();
    }

    private void applyHeaders(HttpURLConnection urlConnection) {
        for (Map.Entry<String, String> param : headerParams.entrySet()) {
            urlConnection.setRequestProperty(param.getKey(), param.getValue());
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void performParsing(String TAG, String postResult, Type type) {

        jsonParsing = false;
        classType = null;

        if (postResult != null) {
            Gson gson = new GsonBuilder().create();
            try {
                Object response = gson.fromJson(postResult, type);
                responseListener.objectResponse(TAG, response);
            } catch (JsonSyntaxException jException) {
                jException.printStackTrace();
                String errorString;
                errorString = "Some Issue in Json Syntax. Unable to parse it";
                Log.d("----err jsonString:", postResult);
                Log.d("----err json:", jException.getMessage());
                responseListener.negativeResponse(TAG, errorString);

            } catch (Exception e) {
                e.printStackTrace();
                String errorString;
                errorString = "Some Issue in Json Syntax. Unable to parse it";
                Log.d("----err jsonString:", postResult);
                Log.d("----err json:", e.getMessage());
                responseListener.negativeResponse(TAG, errorString);
            }
        } else {
            String error;
            error = "Error in Response";
            responseListener.negativeResponse(TAG, error);
        }


    }
}
