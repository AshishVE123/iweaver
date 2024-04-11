package com.social.iweaver.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.RegistrationResponse;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.databinding.ActivitySignUpBinding;
import com.social.iweaver.utils.CustomAlertDialog;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.InputFieldValidator;
import com.social.iweaver.utils.JsonBodyCreator;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.RegisterViewModel;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper databaseHelper;
    private CustomAlertDialog alertDialog;
    private CustomLoader loader;
    private RegisterViewModel signUpModel;
    private JsonBodyCreator jsonBody;
    private CountryCodePicker codePicker;
    private String gender, countryCode, birthDate = null;
    private RadioGroup genderGroup;
    private TextView title, signIn;
    private EditText userName, userEmail, userMobile, userPassword, confirmPassword, userDateofBirth;

    private Button buttonSubmit, back;
    private CalendarView calendarView;
    private LinearLayout llCalendarlayout;
    private ActivitySignUpBinding signUpBinding;

    private RadioButton genderMale, genderFemale;

    private InputFieldValidator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());
        title = signUpBinding.layoutInclude.tvTitleText;
        title.setText(getResources().getString(R.string.signupTitle));
        back = signUpBinding.layoutInclude.btBack;
        validator = InputFieldValidator.getInstance(this);
        signIn = signUpBinding.tvSignIn;
        userName = signUpBinding.etName;
        userEmail = signUpBinding.etEmail;
        userMobile = signUpBinding.etPhone;
        userPassword = signUpBinding.etPassword;
        confirmPassword = signUpBinding.etConfirmPassword;
        buttonSubmit = signUpBinding.btSignUp;
        genderMale = signUpBinding.rbMale;
        genderFemale = signUpBinding.rbFemale;
        genderGroup = signUpBinding.radioGroup;
        calendarView = signUpBinding.calendarView;
        llCalendarlayout = signUpBinding.llCalendar;
        userDateofBirth = signUpBinding.etDateofbirthPassword;
        codePicker = signUpBinding.ccp;
        databaseHelper = new DatabaseHelper(SignUpActivity.this);
        alertDialog = CustomAlertDialog.getInstance(this);
        jsonBody = JsonBodyCreator.getInstance();
        buttonSubmit.setOnClickListener(this);
        signIn.setOnClickListener(this);
        back.setOnClickListener(this);
        loader = CustomLoader.getInstance();
        countryCode = codePicker.getDefaultCountryCodeWithPlus();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year,
                                            int month,
                                            int dayOfMonth) {

                llCalendarlayout.setVisibility(View.GONE);
                //  String msg = "Selected date Day: " + i2 + " Month : " + (i1 + 1) + " Year " + i;
                if (month < 10) {
                    birthDate
                            = dayOfMonth + "-"
                            + "0" + (month + 1) + "-" + year;

                    userDateofBirth.setText(birthDate);
                } else {
                    birthDate
                            = dayOfMonth + "-"
                            + (month + 1) + "-" + year;

                    userDateofBirth.setText(birthDate);
                }


            }
        });
        userDateofBirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userDateofBirth.getWindowToken(), 0);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= userDateofBirth.getRight() - userDateofBirth.getTotalPaddingRight()) {
                        // your action for drawable click event
                        if (llCalendarlayout.getVisibility() == View.VISIBLE)
                            llCalendarlayout.setVisibility(View.GONE);
                        else
                            llCalendarlayout.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                //Toast.makeText(SignUpActivity.this, "outside  edittext clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        codePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {


            @Override
            public void onCountrySelected() {
                countryCode = codePicker.getSelectedCountryCodeWithPlus();
            }
        });
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_male: {
                        gender = "male";
                        break;
                    }
                    case R.id.rb_female: {
                        gender = "female";
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signIn:
            case R.id.bt_back: {
                finish();
                break;
            }
            case R.id.bt_signUp: {
                int errorType = validator.isInputstringNull(userName.getText().toString(), userEmail.getText().toString(), userMobile.getText().toString(), userPassword.getText().toString(), confirmPassword.getText().toString(), gender, userDateofBirth.getText().toString());
                if (errorType == 1) {
                    userName.setError("Please enter name");
                    userName.requestFocus();
                } else if (errorType == 2) {
                    userEmail.setError("Please enter email");
                    userEmail.requestFocus();
                } else if (errorType == 3) {
                    userMobile.setError("Please enter contact details");
                    userMobile.requestFocus();
                } else if (errorType == 4) {
                    userPassword.setError("Please enter password");
                    userPassword.requestFocus();
                } else if (errorType == 5) {
                    confirmPassword.setError("Please enter password again");
                    confirmPassword.requestFocus();
                } else if (errorType == 6) {
                    userEmail.setError("Please enter valid email");
                    userEmail.requestFocus();
                } else if (errorType == 7) {
                    confirmPassword.setError("Password do not match. Please enter again.");
                    confirmPassword.requestFocus();
                } else if (errorType == 8) {
                    userDateofBirth.setError("Please enter date of birth.");
                    userDateofBirth.requestFocus();
                } else if (errorType == 9) {
                    Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkUtils.getConnectivityStatusString(SignUpActivity.this)) {
                        loader.showLoading(SignUpActivity.this, "Please wait. We are creating your account.");
                        callRegisterApi(JsonBodyCreator.getSignupBody(userName.getText().toString(), userEmail.getText().toString(), countryCode + "" + userMobile.getText().toString(), userPassword.getText().toString(), confirmPassword.getText().toString(), userDateofBirth.getText().toString(), gender));
                    } else {
                        startActivity(new Intent(SignUpActivity.this, NetworkIssueActivity.class));
                        finish();
                    }
                }

                break;
            }
        }
    }

    /**
     * @param object
     */
    public void callRegisterApi(JsonObject object) {
        signUpModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegisterViewModel.class);
        signUpModel.initiateRegisterProcess(SignUpActivity.this, object)
                .observe(SignUpActivity.this, new Observer<RegistrationResponse>() {
                    @Override
                    public void onChanged(RegistrationResponse signupResponse) {
                        loader.hideLoading();
                        signUpModel = null;
                        if (signupResponse != null) {
                            alertDialog.showInfoAlertDialog(SignUpActivity.this, "Welcome to I-Weaver Network. Please login now.");
                            System.out.println("Sign up response::" + signupResponse);
                        } else {
                            loader.showSnackBar(SignUpActivity.this, ErrorReponseParser.errorMsg,false);
                        }
                    }
                });
    }
}