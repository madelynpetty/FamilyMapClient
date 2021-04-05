package com.example.familymap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;


public class SettingsActivity extends AppCompatActivity {
    public static boolean lifeStoryLines = true;
    public static boolean familyTreeLines = true;
    public static boolean spouseLines = true;
    public static boolean fathersSide = true;
    public static boolean mothersSide = true;
    public static boolean maleEvents = true;
    public static boolean femaleEvents = true;

    private Switch lifeStoryLinesSwitch;
    private Switch familyTreeLinesSwitch;
    private Switch spouseLinesSwitch;
    private Switch fathersSideSwitch;
    private Switch mothersSideSwitch;
    private Switch maleEventsSwitch;
    private Switch femaleEventsSwitch;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lifeStoryLinesSwitch = this.findViewById(R.id.lifeStoryLinesSwitch);
        familyTreeLinesSwitch = this.findViewById(R.id.familyTreeLinesSwitch);
        spouseLinesSwitch = this.findViewById(R.id.spouseLinesSwitch);
        fathersSideSwitch = this.findViewById(R.id.fathersSideSwitch);
        mothersSideSwitch = this.findViewById(R.id.mothersSideSwitch);
        maleEventsSwitch = this.findViewById(R.id.maleEventsSwitch);
        femaleEventsSwitch = this.findViewById(R.id.femaleEventsSwitch);
        logoutButton = this.findViewById(R.id.logoutButton);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == lifeStoryLinesSwitch) {
                    lifeStoryLines = isChecked;
                }
                else if (buttonView == familyTreeLinesSwitch) {
                    familyTreeLines = isChecked;
                }
                else if (buttonView == spouseLinesSwitch) {
                    spouseLines = isChecked;
                }
                else if (buttonView == fathersSideSwitch) {
                    fathersSide = isChecked;
                }
                else if (buttonView == mothersSideSwitch) {
                    mothersSide = isChecked;
                }
                else if (buttonView == maleEventsSwitch) {
                    maleEvents = isChecked;
                }
                else if (buttonView == femaleEventsSwitch) {
                    femaleEvents = isChecked;
                }
            }
        };

        lifeStoryLinesSwitch.setOnCheckedChangeListener(listener);
        familyTreeLinesSwitch.setOnCheckedChangeListener(listener);
        spouseLinesSwitch.setOnCheckedChangeListener(listener);
        fathersSideSwitch.setOnCheckedChangeListener(listener);
        mothersSideSwitch.setOnCheckedChangeListener(listener);
        maleEventsSwitch.setOnCheckedChangeListener(listener);
        femaleEventsSwitch.setOnCheckedChangeListener(listener);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //TODO logout user and take to loginfragment again
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
//            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}