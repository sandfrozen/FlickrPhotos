package fsv.a5us.flickrphotos;

import android.app.FragmentTransaction;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    private ArrayList<FlickrPhoto> mPhotos = new ArrayList<FlickrPhoto>();
    public final static String API_KEY = "aa9d2aba397225569a63fdd06e68c910";
    public final static String NUM_PHOTOS = "12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if( isOnline() ){
            LoadPhotos task = new LoadPhotos();
            task.execute();
        } else{
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this.getApplicationContext(), "Aby pobrać zdjęcia, musisz się połączyć z siecią!", Toast.LENGTH_SHORT).show();
        }
    }
    public ArrayList<FlickrPhoto> getPhotos(){
        return mPhotos;
    }

    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void showList(){
        PhotoListFragment photoListFragment = new PhotoListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.listView, photoListFragment.getParentFragment()); //
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private class LoadPhotos extends AsyncTask<String, String, Long>{

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Long result) {
            if(result==0){
                showList();
            }else{
                Toast.makeText(MainActivity.this.getApplicationContext(), "Nie udało się wczytać zdjęć.", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected Long doInBackground(String... params) {
            HttpURLConnection connection = null;
            try{
                URL dataUrl = new URL(
                        "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=" + API_KEY
                                + "&per_page=" + NUM_PHOTOS
                                + "&format=json&nojsoncallback=1"
                );
                connection = (HttpURLConnection) dataUrl.openConnection();
                connection.connect();
                int status = connection.getResponseCode();
                if( status == 200 ){
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Połączyło.", Toast.LENGTH_SHORT).show();
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String responseString;
                    StringBuilder sb = new StringBuilder();
                    while( (responseString = reader.readLine()) != null ){
                        sb = sb.append(responseString);
                    }
                    String photoData = sb.toString();
                    Log.d("PHOTOS", photoData);
                    mPhotos = FlickrPhoto.makePhotoList(photoData);
                    return (0l);
                }
                else{
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Nie połączyło.", Toast.LENGTH_SHORT).show();
                    return (1l);
                }
            } catch (MalformedURLException e){
                e.printStackTrace();
                return (1l);
            }
            catch (IOException e){
                e.printStackTrace();
                return (1l);
            }
            catch (NullPointerException e){
                e.printStackTrace();
                return (1l);
            }
            catch (JSONException e){
                e.printStackTrace();
                return (1l);
            }finally {
                connection.disconnect();
            }
        }
    }
}
