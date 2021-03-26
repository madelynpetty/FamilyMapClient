package com.example.familymap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Utils.StringUtil;


public class LoginFragment extends Fragment {
    EditText serverHost, serverPort, username, password, firstName, lastName, email;
    RadioButton genderMale, genderFemale;
    Button registerButton, loginButton;

//    private static LoginFragment loginFragment;
//    public static LoginFragment getInstance() {
//        if (loginFragment != null) {
//            loginFragment = new LoginFragment();
//        }
//        return loginFragment;
//    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serverHost = view.findViewById(R.id.serverHostField);
        serverPort = view.findViewById(R.id.serverPortField);
        username = view.findViewById(R.id.usernameField);
        password = view.findViewById(R.id.passwordField);
        firstName = view.findViewById(R.id.firstNameField);
        lastName = view.findViewById(R.id.lastNameField);
        email = view.findViewById(R.id.emailField);
        genderMale = view.findViewById(R.id.genderMale);
        genderFemale = view.findViewById(R.id.genderFemale);


        registerButton = view.findViewById(R.id.registerButton);
        loginButton = view.findViewById(R.id.loginButton);

        registerButton.setEnabled(false);
        loginButton.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Attempting to login", Toast.LENGTH_SHORT).show();
                try {
                    LoginRequest loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());
                    LoginTask loginTask = new LoginTask();
                    loginTask.execute(loginRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "Attempting to register", Toast.LENGTH_SHORT).show();
//                try {
//                    RegisterRequest registerRequest = new RegisterRequest();
//                    LoginTask loginTask = new LoginTask();
//                    loginTask.execute(loginRequest);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private class LoginTask extends AsyncTask<LoginRequest, Integer, LoginResult> {
        @Override
        protected LoginResult doInBackground(LoginRequest... loginRequests) {
            LoginResult loginResult = null;
            try {
                //TODO fix this url
                URL url = new URL( "http://10.0.2.2:8080/user/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                connection.connect();

                Gson gson = new Gson();
                StringUtil.writeStringToStream(gson.toJson(loginRequests[0]), connection.getOutputStream());
                connection.getOutputStream().close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String json = StringUtil.getStringFromInputStream(connection.getInputStream());
                    loginResult = gson.fromJson(json, LoginResult.class);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return loginResult;
        }

        public void onPostExecute(LoginResult loginResult) {
            if (loginResult.success) {
                Toast.makeText(getContext(), "Login was successful", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Login was unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setButtonState() {
        if (serverHost.length() > 0 && serverPort.length() > 0 && username.length() > 0 && password.length() > 0) {
            loginButton.setEnabled(true);
        }
        else {
            loginButton.setEnabled(false);
        }

        if (serverHost.length() > 0 && serverPort.length() > 0 && username.length() > 0 && password.length() > 0
            && firstName.length() > 0 && lastName.length() > 0 && email.length() > 0) {
            registerButton.setEnabled(true);
        }
        else {
            registerButton.setEnabled(false);
        }
    }

}