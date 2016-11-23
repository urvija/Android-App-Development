package edu.sdsu.cs.cs646.assignment2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

public class CountryListFragment extends ListFragment {

    private static final String TAG = "CountryListFragment";

    private IndiaSportsListFragment mIndiaSportsListFragment;
    private USASportsListFragment mUSASportsListFragment;
    private MexicoSportsListFragment mMexicoSportsListFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<CharSequence> countryArrayAdapter = ArrayAdapter.createFromResource
                (getActivity(), R.array.country_array, android.R.layout.simple_list_item_activated_1);
        setListAdapter(countryArrayAdapter);

        final ListView listView = getListView();

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
                listView.setItemChecked(position, true);
                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String activityName = getActivity().getLocalClassName().toString();
        if(activityName.equals("ListActivity")) {
            Log.d(TAG, "position onListItemClick: " + position);
            if(position == 0)
            {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                mIndiaSportsListFragment = new IndiaSportsListFragment();
                transaction.replace(R.id.sportsListFragmentContainer, mIndiaSportsListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            if(position == 1)
            {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                mUSASportsListFragment = new USASportsListFragment();
                transaction.replace(R.id.sportsListFragmentContainer, mUSASportsListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            if(position == 2)
            {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                mMexicoSportsListFragment = new MexicoSportsListFragment();
                transaction.replace(R.id.sportsListFragmentContainer, mMexicoSportsListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
         }
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
}