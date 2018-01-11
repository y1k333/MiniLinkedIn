package com.yk.minilinkedin;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.yk.minilinkedin.model.BasicInfo;
import com.yk.minilinkedin.model.Education;
import com.yk.minilinkedin.model.Experience;
import com.yk.minilinkedin.model.Project;
import com.yk.minilinkedin.util.DateUtils;
import com.yk.minilinkedin.util.ImageUtils;
import com.yk.minilinkedin.util.ModelUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int REQ_CODE_EDIT_EDUCATION = 100;
    private static final int REQ_CODE_EDIT_EXPERIENCE = 101;
    private static final int REQ_CODE_EDIT_PROJECT = 102;
    private static final int REQ_CODE_EDIT_BASIC_INFO = 103;

    private static final String MODEL_EDUCATIONS = "educations";
    private static final String MODEL_EXPERIENCES = "experiences";
    private static final String MODEL_PROJECTS = "projects";
    private static final String MODEL_BASIC_INFO = "basic_info";

    private BasicInfo basicInfo;
    private List<Education> educations;
    private List<Experience> experiences;
    private List<Project> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        setupUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case REQ_CODE_EDIT_BASIC_INFO:
                    BasicInfo basicInfo = data.getParcelableExtra(BasicInfoEditActivity.KEY_BASIC_INFO);
                    updateBasicInfo(basicInfo);
                    break;
                case REQ_CODE_EDIT_EDUCATION:
                    String educationId = data.getStringExtra(EducationEditActivity.KEY_EDUCATION_ID);
                    if (educationId != null) {
                        deleteEducation(educationId);
                    } else {
                        Education education = data.getParcelableExtra(EducationEditActivity.KEY_EDUCATION);
                        updateEducation(education);
                    }
                    break;
            }
        }
    }

    private void loadData() {
        BasicInfo savedBasicInfo =
                ModelUtils.read(this, MODEL_BASIC_INFO, new TypeToken<BasicInfo>(){});
        basicInfo = savedBasicInfo == null ? new BasicInfo() : savedBasicInfo;
        List<Education> savedEducation =
                ModelUtils.read(this, MODEL_EDUCATIONS, new TypeToken<List<Education>>(){});
        educations = savedEducation == null ? new ArrayList<Education>() : savedEducation;
//        List<Experience> savedExperience =
//                ModelUtils.read(this, MODEL_EDUCATIONS, new TypeToken<List<Experience>>(){});
//        experiences = savedExperience == null ? new ArrayList<Experience>() : savedExperience;
//        List<Project> savedProjects =
//                ModelUtils.read(this, MODEL_PROJECTS, new TypeToken<List<Project>>(){});
//        projects = savedProjects == null ? new ArrayList<Project>() : savedProjects;
    }


    private void setupUI() {
        setContentView(R.layout.activity_main);

        final ImageButton editBasicInfo = (ImageButton) findViewById(R.id.edit_basic_info);
        editBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BasicInfoEditActivity.class);
                intent.putExtra(BasicInfoEditActivity.KEY_BASIC_INFO, basicInfo);
                startActivityForResult(intent, REQ_CODE_EDIT_BASIC_INFO);
            }
        });

        ImageButton addEducationBtn = (ImageButton) findViewById(R.id.add_education_btn);
        addEducationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EducationEditActivity.class);
                startActivityForResult(intent, REQ_CODE_EDIT_EDUCATION);
            }
        });

        setupBasicInfo();
        setupEducations();
    }

    private void setupBasicInfo() {
        ((TextView) findViewById(R.id.name)).setText(TextUtils.isEmpty(basicInfo.name) ? "Your name" : basicInfo.name);
        ((TextView) findViewById(R.id.email)).setText(TextUtils.isEmpty(basicInfo.email) ? "Your email" : basicInfo.email);

        ImageView userPic = (ImageView) findViewById(R.id.user_picture);
        if (basicInfo.imageUri != null) {
            ImageUtils.loadImage(this, basicInfo.imageUri, userPic);
        } else {
            userPic.setImageResource(R.drawable.user_ghost);
        }

    }

    private void setupEducations() {
        LinearLayout educationsLayout = (LinearLayout) findViewById(R.id.education_list);
        //new LinearLayout to replace the original education_item(new .xml) then addView()
        educationsLayout.removeAllViews();
        for (Education education : educations) {
            View educationView = getLayoutInflater().inflate(R.layout.education_item, null);
            //education_item is in a new .xml file so we need to use getLayoutInflater() to get the view.
            setupEducation(educationView, education);
            educationsLayout.addView(educationView);
        }
    }

    private void setupEducation(View educationView, final Education education) {
        // finial here so inner class object can make a copy of this variable and store the copy in heap.
        String dateString = DateUtils.dateToString(education.startDate)
                + " ~ " + DateUtils.dateToString(education.endDate);
        ((TextView) educationView.findViewById(R.id.education_school))
                .setText(education.school + " " + education.major + " (" + dateString + ")");
        ((TextView) educationView.findViewById(R.id.education_courses))
                .setText(formatItems(education.courses));

        ImageButton editEducationBtn = (ImageButton) educationView.findViewById(R.id.edit_education_btn);
        editEducationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EducationEditActivity.class);
                intent.putExtra(EducationEditActivity.KEY_EDUCATION, education);
                startActivityForResult(intent, REQ_CODE_EDIT_EDUCATION);
            }
        });
    }

    public static String formatItems(List<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String item: items) {
            sb.append(' ').append('-').append(' ').append(item).append('\n');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void updateBasicInfo(BasicInfo basicInfo) {
        ModelUtils.save(this, MODEL_BASIC_INFO, basicInfo);
        this.basicInfo = basicInfo;
        setupBasicInfo();
    }

    private void updateEducation(Education education) {
        boolean found = false;
        for (int i = 0; i < educations.size(); i++) {
            Education e = educations.get(i);
            if (TextUtils.equals(e.id, education.id)) {
                found = true;
                educations.set(i, education);
                break;
            }
        }

        if (!found) {
            educations.add(education);
        }

        ModelUtils.save(this, MODEL_EDUCATIONS, educations);
        setupEducations();
    }


    private void deleteEducation(String educationId) {
        for (int i = 0; i < educations.size(); ++i) {
            Education e = educations.get(i);
            if (TextUtils.equals(e.id, educationId)) {
                educations.remove(i);
                break;
            }
        }

        ModelUtils.save(this, MODEL_EDUCATIONS, educations);
        setupEducations();
    }
}
