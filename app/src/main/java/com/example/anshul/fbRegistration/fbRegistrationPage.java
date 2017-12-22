package com.example.anshul.fbRegistration;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.password;


public class fbRegistrationPage extends AppCompatActivity implements View.OnClickListener {

    EditText UserName, UserEmail, Password, UserDOB;
    String UName, UEid, Pwd, DOB;
    //    String username,password,phone;
    Button RegisterButton;
    boolean email_result = false;
    boolean validDate = false;
    private Vibrator vib;
    Animation animShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_registration_page);

        UserName = (EditText) findViewById(R.id.UserName);
        UserEmail = (EditText) findViewById(R.id.UserEmail);
        Password = (EditText) findViewById(R.id.Password);
        RegisterButton = (Button) findViewById(R.id.RegisterButton);
        UserDOB = (EditText) findViewById(R.id.UserDOB);
        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        RegisterButton.setOnClickListener(this);

    }

    public void onRegistrationSuccess() {
        //After Successful Registration
        //Retrofit to retrieve JSON data from server.

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).build();
//        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(10, TimeUnit.SECONDS);
//        client.setReadTimeout(30, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();
        String username = UserEmail.getText().toString();
        String password = Password.getText().toString();
        String phone = UserDOB.getText().toString();

        //Passing Interface to create an implementation.
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            Registrationinfo registerationInfo = new Registrationinfo(username, password, phone);
            registerationInfo.setUsername(username);
            registerationInfo.setPassword(password);
            registerationInfo.setPhone(phone);
            apiService.savePost(registerationInfo).enqueue(new Callback<String>() {
                //        apiService.savePost(username, password, phone).enqueue(new Callback<Registrationinfo>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
//                            showResponse(
                               Toast.makeText(getApplicationContext(),response.body().toString(),Toast.LENGTH_SHORT).show();
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        Toast.makeText(getApplicationContext(), "Registeration Successful.. ", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 200) {
                        Toast.makeText(getApplicationContext(), "Registeration Successful.. ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 500) {
                        Toast.makeText(getApplicationContext(), "Some Error occured ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 400) {
                        Log.d("Error code : ", "" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getApplicationContext(), "Registeration failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void register() {
        initialize();
        if (!Validate()) {
        } else {
            onRegistrationSuccess();

        }
    }

    public boolean Validate() {
        boolean valid = true;
        if (UName.isEmpty() || UName.length() >= 32) {
            UserName.setError("Please Enter Valid name");
        }
        if (UEid.isEmpty()) {
            UserEmail.setError("Please Enter Email");

        } else {
            isValidEmailAddress(UEid);
            if (email_result) {
                UserEmail.setText(UEid);
            } else {
                UserEmail.setError("Enter valid email address.");
            }
        }
        if (Pwd.isEmpty()) {
            Password.setError("Please enter password");
        } else {
            if (Pwd.length() >= 8 && isValidPassword(Password.getText().toString())) {
                Password.setText(Pwd);
            } else {
                Password.setError("1.Password must contain alphanumeric value +\n+" +
                        "2.Atleast one special character" + "\n" +
                        "3.Password length mustbe atleast 8");
            }
        }
        if (DOB.isEmpty()) {
            UserDOB.setError("Please enter DOB");
        } else {
            isvalidDate(DOB);
            if (validDate) {
                UserDOB.setText(DOB);
            } else {
                UserDOB.setError("Age should be greater than or equal to 18");
            }
        }

        return valid;
    }

    public boolean isvalidDate(String DateOfBirth) {
        String[] s = DateOfBirth.split("/");
        int day = Integer.parseInt(s[0]);
        int month = Integer.parseInt(s[1]);
        int year = Integer.parseInt(s[2]);

        if (year >= 1947 && year <= 1999) {
            if (month >= 1 && month <= 12) {
                if (year % 4 == 0 || year % 100 == 0 || year % 400 == 0) {
                    if (month == 2) {
                        if (day >= 1 && day <= 29) {
                            validDate = true;
                            return validDate;
                        } else {
                            UserDOB.setError("February has 29 days in leap year.");
                        }
                    }
                } else if (month == 2) {
                    if (day >= 1 && day <= 28) {
                        validDate = true;
                        return validDate;
                    } else {
                        UserDOB.setError("February has 28 days in non leap year.");
                    }
                } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                    if (day >= 1 && day <= 30) {
                        validDate = true;
                        return validDate;
                    } else {
                        return validDate;
                    }
                }
            } else {
                UserDOB.setError("Month range should be 1 to 12");
            }

        } else {
            UserDOB.setError("You are underage.");
        }
        return validDate;
    }

    public boolean isValidEmailAddress(String email) {

        if (email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))//email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        {
            email_result = true;
            return email_result;
        }
        return email_result;
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void initialize() {
        UName = UserName.getText().toString();
        UEid = UserEmail.getText().toString();
        Pwd = Password.getText().toString();
        DOB = UserDOB.getText().toString();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.RegisterButton: {
                register();
            }
        }
    }
//    public void sendPost(String username, String password, String phone) {
//        ApiInterface.savePost(username,password,phone).enqueue(new Callback<Registrationinfo>() {
//            @Override
//            public void onResponse(Call<Registrationinfo> call, Response<Registrationinfo> response) {
//                if(response.isSuccessful()) {
//                    showResponse(response.body().toString());
//                    Log.i(TAG, "post submitted to API." + response.body().toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Registrationinfo> call, Throwable t) {
//                Log.e(TAG, "Unable to submit post to API.");
//
//            }
//
//        });
//    }

//@Override
//public String toStrings(String username, String password, String phone) {
//    return "{" +
//            "username :'"+'"' + username +'"'+ '\'' +
//            ", password :'"+'"' + password +'"' +'\'' +
//            ", phone : " +'"'+ phone +'"'+
//            '}';
//}
}
