package com.example.familymap;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.Set;

import Result.EventListResult;
import Utils.Globals;
import Utils.Settings;


public class SettingsActivity extends AppCompatActivity {
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
        lifeStoryLinesSwitch.setChecked(Settings.getInstance().lifeStoryLines);

        familyTreeLinesSwitch = this.findViewById(R.id.familyTreeLinesSwitch);
        familyTreeLinesSwitch.setChecked(Settings.getInstance().familyTreeLines);

        spouseLinesSwitch = this.findViewById(R.id.spouseLinesSwitch);
        spouseLinesSwitch.setChecked(Settings.getInstance().spouseLines);

        fathersSideSwitch = this.findViewById(R.id.fathersSideSwitch);
        fathersSideSwitch.setChecked(Settings.getInstance().fathersSide);

        mothersSideSwitch = this.findViewById(R.id.mothersSideSwitch);
        mothersSideSwitch.setChecked(Settings.getInstance().mothersSide);

        maleEventsSwitch = this.findViewById(R.id.maleEventsSwitch);
        maleEventsSwitch.setChecked(Settings.getInstance().maleEvents);

        femaleEventsSwitch = this.findViewById(R.id.femaleEventsSwitch);
        femaleEventsSwitch.setChecked(Settings.getInstance().femaleEvents);

        logoutButton = this.findViewById(R.id.logoutButton);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == lifeStoryLinesSwitch) {
                    Settings.getInstance().lifeStoryLines = isChecked;
                }
                else if (buttonView == familyTreeLinesSwitch) {
                    Settings.getInstance().familyTreeLines = isChecked;
                }
                else if (buttonView == spouseLinesSwitch) {
                    Settings.getInstance().spouseLines = isChecked;
                }
                else if (buttonView == fathersSideSwitch) {
                    Settings.getInstance().fathersSide = isChecked;
                }
                else if (buttonView == mothersSideSwitch) {
                    Settings.getInstance().mothersSide = isChecked;
                }
                else if (buttonView == maleEventsSwitch) {
                    Settings.getInstance().maleEvents = isChecked;
                }
                else if (buttonView == femaleEventsSwitch) {
                    Settings.getInstance().femaleEvents = isChecked;
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
        }

        return super.onOptionsItemSelected(item);
    }
}