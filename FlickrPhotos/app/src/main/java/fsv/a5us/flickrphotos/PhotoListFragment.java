package fsv.a5us.flickrphotos;

import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A5US on 04.01.2017.
 */

public class PhotoListFragment extends ListFragment {
    String[] mTitles;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity currentActivity = (MainActivity) this.getActivity();
        ArrayList<FlickrPhoto> photos = currentActivity.getPhotos();
        mTitles = new String[photos.size()];
        for (int i=0; i<photos.size(); i++) {
            mTitles[i] = photos.get(i).title;
        }
        setListAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, mTitles));
    }
}
