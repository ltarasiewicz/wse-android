package com.example.wse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wse.databinding.ActivityMainBinding;
import com.example.wse.types.AccelerationUnit;
import com.example.wse.types.DashboardSettings;
import com.example.wse.types.DashboardTheme;

public class MainActivity extends AppCompatActivity {
    private DashboardSettings viewModel;
    private DashboardSettings parcelableSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parcelableSettings = new DashboardSettings();

        @NonNull ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater()); // handle to XML activity_main.xml
        setContentView(binding.getRoot()); // root element of activity_main.xml
        binding.setViewModel(parcelableSettings); // bind settings to activity_main.xml
        viewModel = binding.getViewModel();

        binding.unitSelectionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.kmhRadioButton) {
                viewModel.setAccelerationUnit(AccelerationUnit.KILOMETER_PER_HOUR);
            } else if (checkedId == R.id.msRadioButton) {
                viewModel.setAccelerationUnit(AccelerationUnit.METER_PER_SECOND);
            }
        });

        binding.decimalPlacesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int decimalPrecision, boolean fromUser) {
                viewModel.setDecimalPrecision(decimalPrecision);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

        binding.themeSelectRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.lightThemeRadioBtn) {
                viewModel.setTheme(DashboardTheme.LIGHT);
            } else if (checkedId == R.id.darkThemeRadioBtn) {
                viewModel.setTheme(DashboardTheme.DARK);
            }
        });

        binding.editPersonName.setOnEditorActionListener((v, actionId, event) -> {
            viewModel.setUserName(binding.editPersonName.getText().toString());
            return false;
        });

        binding.navigateToCarDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CarDashboard.class).putExtra("settings", parcelableSettings);
            startActivity(intent);
        });
    }
}
