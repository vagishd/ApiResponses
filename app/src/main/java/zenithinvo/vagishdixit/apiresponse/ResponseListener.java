package zenithinvo.vagishdixit.apiresponse;

/**
 * Created by Vagish Dixit on 2/21/2018.
 */

public interface ResponseListener {
    public void stringResponse(String TAG, String response);

    public void objectResponse(String TAG, Object responseObj);

    public void negativeResponse(String TAG, String response);
}
