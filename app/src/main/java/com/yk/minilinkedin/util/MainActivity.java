package com.yk.minilinkedin.util;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yk.minilinkedin.R;
import com.yk.minilinkedin.model.BasicInfo;
import com.yk.minilinkedin.model.Education;
import com.yk.minilinkedin.model.Experience;
import com.yk.minilinkedin.model.Project;

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
        setContentView(R.layout.activity_main);
    }
}
