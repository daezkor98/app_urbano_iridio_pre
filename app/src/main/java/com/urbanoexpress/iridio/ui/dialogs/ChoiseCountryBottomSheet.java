package com.urbanoexpress.iridio.ui.dialogs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.BottomSheetChoiseCountryBinding;
import com.urbanoexpress.iridio.ui.adapter.CountriesAdapter;
import com.urbanoexpress.iridio.ui.fragment.ConfigPhoneFragment;
import com.urbanoexpress.iridio.ui.model.PaisItem;

import java.util.ArrayList;
import java.util.List;

public class ChoiseCountryBottomSheet extends BottomSheetDialogFragment
        implements CountriesAdapter.OnCountriesListener {

    private BottomSheetChoiseCountryBinding binding;
    private List<PaisItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetChoiseCountryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    @Override
    public void onCountryClick(int position) {
        ((ConfigPhoneFragment) getParentFragment()).onCountrySelected(items.get(position).getIso());
        dismiss();
    }

    private void showPaises(List<PaisItem> paisItems) {
        try {
            CountriesAdapter adapter = new CountriesAdapter(getActivity(), paisItems);
            adapter.setListener(this);
            binding.rvCountries.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void setupViews() {
        binding.rvCountries.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvCountries.setHasFixedSize(true);

        loadPaises();
    }

    private void loadPaises() {
        PaisItem item = new PaisItem(
                "Chile (+56)",
                "cl",
                R.drawable.flag_chile,
                Color.parseColor("#00FFFFFF"),
                false
        );
        items.add(item);

        /*item = new PaisItem(
                "Ecuador (+593)",
                "ec",
                R.drawable.flag_ecuador,
                Color.parseColor("#00FFFFFF"),
                false
        );
        items.add(item);*/

        item = new PaisItem(
                "Perú (+51)",
                "pe",
                R.drawable.flag_peru,
                Color.parseColor("#00FFFFFF"),
                false
        );
        items.add(item);

        showPaises(items);
    }

}