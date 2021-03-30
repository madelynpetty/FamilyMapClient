package com.example.familymap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;

import Models.AuthToken;
import Models.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.PersonListResult;
import Result.RegisterResult;
import Utils.StringUtil;


public class LoginFragment extends Fragment {
    private EditText serverHost, serverPort, username, password, firstName, lastName, email;
    private RadioButton genderMale, genderFemale;
    private Button registerButton, loginButton;
    private LoginResult loginResult = null;

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

        //TODO DELETE THESE OUT LATER
        serverHost.setText("10.0.2.2");
        serverPort.setText("8080");

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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Attempting to register", Toast.LENGTH_SHORT).show();
                try {
                    RegisterRequest registerRequest = new RegisterRequest(username.getText().toString(), password.getText().toString(), email.getText().toString(),
                            firstName.getText().toString(), lastName.getText().toString(), genderFemale.isChecked() ? "f" : "m");
                    RegisterTask registerTask = new RegisterTask();
                    registerTask.execute(registerRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class LoginTask extends AsyncTask<LoginRequest, Integer, LoginResult> {
        @Override
        protected LoginResult doInBackground(LoginRequest... loginRequests) {
            try {
                URL url = new URL( "http://" + serverHost.getText().toString() + ":" + serverPort.getText().toString() + "/user/login");
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
                else {
                    Thread thread = new Thread() {
                        public void run() {
                            Looper.prepare();
                            Handler mHandler = new Handler() {
                                public void handleMessage(Message msg) {
                                    Toast.makeText(getContext(), "Error processing request, please try again", Toast.LENGTH_SHORT).show();
                                }
                            };
                            Looper.loop();
                        }
                    };
                    thread.start();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return loginResult;
        }

        @Override
        public void onPostExecute(LoginResult loginResult) {
            if (loginResult == null) {
                Toast.makeText(getContext(), "Login was unsuccessful", Toast.LENGTH_SHORT).show();
            }
            else if (loginResult.success) {
                Toast.makeText(getContext(), "Login was successful", Toast.LENGTH_SHORT).show();

                FamilyDataTask familyDataTask = new FamilyDataTask();
                familyDataTask.execute(loginResult.authtoken);
            }
            else {
                Toast.makeText(getContext(), "Login was unsuccessful", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class RegisterTask extends AsyncTask<RegisterRequest, Integer, RegisterResult> {
        @Override
        protected RegisterResult doInBackground(RegisterRequest... registerRequests) {
            RegisterResult registerResult = null;
            try {
                URL url = new URL( "http://" + serverHost.getText().toString() + ":" + serverPort.getText().toString() + "/user/register");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                connection.connect();

                Gson gson = new Gson();
                StringUtil.writeStringToStream(gson.toJson(registerRequests[0]), connection.getOutputStream());
                connection.getOutputStream().close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String json = StringUtil.getStringFromInputStream(connection.getInputStream());
                    registerResult = gson.fromJson(json, RegisterResult.class);
                }
                else {
                    Thread thread = new Thread() {
                        public void run() {
                            Looper.prepare();
                            Handler mHandler = new Handler() {
                                public void handleMessage(Message msg) {
                                    Toast.makeText(getContext(), "Error processing request, please try again", Toast.LENGTH_SHORT).show();
                                }
                            };
                            Looper.loop();
                        }
                    };
                    thread.start();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return registerResult;
        }

        @Override
        public void onPostExecute(RegisterResult registerResult) {
            if (registerResult == null) {
                Toast.makeText(getContext(), "Registration was unsuccessful", Toast.LENGTH_SHORT).show();
            }
            else if (registerResult.success) {
                Toast.makeText(getContext(), "Registration was successful", Toast.LENGTH_SHORT).show();
                FamilyDataTask familyDataTask = new FamilyDataTask();
                familyDataTask.execute(registerResult.authtoken);

                ((MainActivity) getActivity()).showMap();
            }
            else {
                Toast.makeText(getContext(), "Registration was unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FamilyDataTask extends AsyncTask<String, Integer, PersonListResult> {
        @Override
        protected PersonListResult doInBackground(String... authTokens) {
            PersonListResult personListResult = null;

            try {
                URL url = new URL( "http://" + serverHost.getText().toString() + ":" + serverPort.getText().toString() + "/person");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);

                connection.addRequestProperty("Authorization", authTokens[0]);

                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    String json = StringUtil.getStringFromInputStream(connection.getInputStream());
                    personListResult = gson.fromJson(json, PersonListResult.class);
                }
                else {
                    Thread thread = new Thread() {
                        public void run() {
                            Looper.prepare();
                            Handler mHandler = new Handler() {
                                public void handleMessage(Message msg) {
                                    Toast.makeText(getContext(), "Error processing request, please try again", Toast.LENGTH_SHORT).show();
                                }
                            };
                            Looper.loop();
                        }
                    };
                    thread.start();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return personListResult;
        }

        @Override
        public void onPostExecute(PersonListResult personListResult) {
            if (personListResult.isSuccess()) {
                if (personListResult.getData().size() == 0) {
                    Toast.makeText(getContext(), "No family for logged in user.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), ((Person) personListResult.getData().get(0)).getFirstName()
                            + " " + ((Person) personListResult.getData().get(0)).getLastName() + " is logged in.", Toast.LENGTH_LONG).show();

                    ((MainActivity) getActivity()).showMap();
                }
            }
            else {
                Toast.makeText(getContext(), "Displaying logged in user was unsuccessful", Toast.LENGTH_SHORT).show();
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