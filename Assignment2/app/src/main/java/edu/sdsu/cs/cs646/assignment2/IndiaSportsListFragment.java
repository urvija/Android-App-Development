package edu.sdsu.cs.cs646.assignment2;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IndiaSportsListFragment extends ListFragment {

    private static final String TAG = "IndiaSportsListFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "position" + position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sportsfragment_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<CharSequence> indiaArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sports_array_india, android.R.layout.simple_list_item_activated_1);
        setListAdapter(indiaArrayAdapter);

        final ListView listView = getListView();

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long id) {
                listView.setItemChecked(position, true);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if(bundle != null) {
            int position = bundle.getInt(Assignment2Constants.KEY_POSITION, -1);
            if(position > -1) {
                getListView().setItemChecked(position, true);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}