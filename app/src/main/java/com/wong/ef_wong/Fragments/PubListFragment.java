package com.wong.ef_wong.Fragments;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wong.ef_wong.Activities.MainActivity;
import com.wong.ef_wong.R;
import com.wong.ef_wong.Adapters.RowAdapter;
import com.wong.ef_wong.beans.Publicacion;

public class PubListFragment extends Fragment {

    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pub_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView)getView().findViewById(R.id.listView);
        final RowAdapter rowAdapter = new RowAdapter(getActivity());
        listView.setAdapter(rowAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity mainActivity = (MainActivity) getActivity();
                Publicacion item = (Publicacion) rowAdapter.getItem(i);
                ((PubMapFragment) mainActivity.getPubMapFragment()).setSelected(item.getKey());
                mainActivity.getNavigationView().getMenu().getItem(1).setChecked(true);
                mainActivity.loadFragment(mainActivity.getPubMapFragment());
            }
        });
    }



}
