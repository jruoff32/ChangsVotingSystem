package co.foodchain.changsvotingsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.PendingIntent.getActivity;



public class ReceiveSMSActivity extends ActionBarActivity {

    static TextView messageBox;
    static String phone;
    ArrayList id;
    ArrayList votecount;

    int test = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reloadtable();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "AM3MHg0KPaM67evDUg0IKdpfNqYwAvuy3ei2Y1NE", "sYJ1XVwh3a0MlP9dEAfbuHxzBSnWtCwS4OoBk9Hu");

        messageBox = (TextView) findViewById(R.id.messageBox);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                reloadtable();
                return true;
            case R.id.noon:
                noontable();
                return true;
            case R.id.oneoclock:
                onetable();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void updateMessageBox(String msg) {




    }

    public static void sendSMSMessage(String phoneNumber, String textMessage, Context context) {
        Log.i("Send SMS", "");
        phone = phoneNumber;

    //check if textmessage is a valid integer TODO
        final String thevote = textMessage;

        final Context appContext = context;

        ParseUser user = new ParseUser();
        user.setUsername(phoneNumber);
        user.setPassword("vote");


        user.put("vote", textMessage);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {

                if (e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("VotingTable");

                    //check if object has already been voted for and
                    //if not make new object in table
                    query.whereEqualTo("voteid", thevote);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> voteList, ParseException e) {
                            if (e == null) {
                                //hasn't been voted yet make object
                                if (voteList.size() == 0) {
                                    ParseObject votingpoll = new ParseObject("VotingTable");
                                    votingpoll.put("voteid", thevote);
                                    votingpoll.put("votes", 1);
                                    votingpoll.saveInBackground();

                                }//has been voted already s increment
                                else {

                                    ParseObject addvote = voteList.get(0);
                                    addvote.increment("votes");
                                    addvote.saveInBackground();

                                }

                            } else {
                                Log.d("votes", "Error: " + e.getMessage());
                            }
                        }
                    });


                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Phone number already voted

                    alert(e.toString(), appContext);

                }
            }
        });


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "We got your vote, thank you!", null, null);
            Toast.makeText(context, "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context,
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void alert(String error, Context theContext) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, "Sorry you already voted!", null, null);
        Toast.makeText(theContext,
                "Sorry you already voted!",
                Toast.LENGTH_LONG).show();
        // new AlertDialog.Builder(this).setMessage(error).setPositiveButton(android.R.string.ok, null).show();
    }
    public static void alertstring(String error, Context theContext) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, "Sorry that is not a valid vote!", null, null);
        Toast.makeText(theContext,
                "Sorry that is not a valid vote!",
                Toast.LENGTH_LONG).show();
        // new AlertDialog.Builder(this).setMessage(error).setPositiveButton(android.R.string.ok, null).show();
    }

    public void reloadtable() {

        getSupportActionBar().setTitle("Vote Results");

        id = new ArrayList();
        votecount = new ArrayList();

        Parse.initialize(this, "AM3MHg0KPaM67evDUg0IKdpfNqYwAvuy3ei2Y1NE", "sYJ1XVwh3a0MlP9dEAfbuHxzBSnWtCwS4OoBk9Hu");

        ParseQuery<ParseObject> votequery = ParseQuery.getQuery("VotingTable");
        votequery.whereEqualTo("true", true);
        votequery.orderByDescending("votes");

        votequery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> voteshow, ParseException e) {
                if (e == null) {
                    TableLayout tableLayout = new TableLayout(getApplicationContext());
                    TableRow tableRow;
                    TextView textView;

                    for (int i = 0; i < voteshow.size(); i++) {
                        ParseObject temp = voteshow.get(i);
                        tableRow = new TableRow(getApplicationContext());
                        for (int j = 0; j < 2; j++) {
                            textView = new TextView(getApplicationContext());

                            if (j == 0) {
                                textView.setText(temp.get("voteid").toString());
                                //get id
                            } else {
                                textView.setText(temp.get("votes").toString());
                                //get vote amount
                            }


                            textView.setPadding(20, 20, 20, 20);
                            tableRow.addView(textView);
                        }
                        tableLayout.addView(tableRow);
                    }
                    setContentView(tableLayout);


                    Log.d("votes", ": " + voteshow.size());
                } else {
                    Log.d("votes", "Error: " + e.getMessage());
                }
            }
        });


    }
    public void noontable() {

    getSupportActionBar().setTitle("12-1 Results");

    id = new ArrayList();
    votecount = new ArrayList();

    Parse.initialize(this, "AM3MHg0KPaM67evDUg0IKdpfNqYwAvuy3ei2Y1NE", "sYJ1XVwh3a0MlP9dEAfbuHxzBSnWtCwS4OoBk9Hu");

        Date noon = new Date();
        noon.setHours(12);
        noon.setMinutes(0);
        noon.setSeconds(0);

        Date onetime = new Date();
        onetime.setHours(13);
        onetime.setMinutes(0);
        onetime.setSeconds(0);



    ParseQuery<ParseObject> votequery = ParseQuery.getQuery("VotingTable");
    votequery.whereEqualTo("true", true);
    votequery.whereGreaterThan("create", noon);
    votequery.whereLessThan("create", onetime);
    votequery.orderByDescending("votes");

    votequery.findInBackground(new FindCallback<ParseObject>() {
        public void done(List<ParseObject> voteshow, ParseException e) {
            if (e == null) {
                TableLayout tableLayout = new TableLayout(getApplicationContext());
                TableRow tableRow;
                TextView textView;

                for (int i = 0; i < voteshow.size(); i++) {
                    ParseObject temp = voteshow.get(i);
                    tableRow = new TableRow(getApplicationContext());
                    for (int j = 0; j < 2; j++) {
                        textView = new TextView(getApplicationContext());

                        if (j == 0) {
                            textView.setText(temp.get("voteid").toString());
                            //get id
                        } else {
                            textView.setText(temp.get("votes").toString());
                            //get vote amount
                        }


                        textView.setPadding(20, 20, 20, 20);
                        tableRow.addView(textView);
                    }
                    tableLayout.addView(tableRow);
                }
                setContentView(tableLayout);


                Log.d("votes", ": " + voteshow.size());
            } else {
                Log.d("votes", "Error: " + e.getMessage());
            }
        }
    });


}
    public void onetable() {

        getSupportActionBar().setTitle("1-2 Results");

        id = new ArrayList();
        votecount = new ArrayList();

        Parse.initialize(this, "AM3MHg0KPaM67evDUg0IKdpfNqYwAvuy3ei2Y1NE", "sYJ1XVwh3a0MlP9dEAfbuHxzBSnWtCwS4OoBk9Hu");

        Date noon = new Date();
        noon.setHours(13);
        noon.setMinutes(0);
        noon.setSeconds(0);

        Date onetime = new Date();
        onetime.setHours(15);
        onetime.setMinutes(0);
        onetime.setSeconds(0);


        ParseQuery<ParseObject> votequery = ParseQuery.getQuery("VotingTable");
        votequery.whereEqualTo("true", true);
        votequery.orderByDescending("votes");
        votequery.whereGreaterThan("create", noon);
        votequery.whereLessThan("create", onetime);

        votequery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> voteshow, ParseException e) {
                if (e == null) {
                    TableLayout tableLayout = new TableLayout(getApplicationContext());
                    TableRow tableRow;
                    TextView textView;

                    for (int i = 0; i < voteshow.size(); i++) {
                        ParseObject temp = voteshow.get(i);
                        tableRow = new TableRow(getApplicationContext());
                        for (int j = 0; j < 2; j++) {
                            textView = new TextView(getApplicationContext());

                            if (j == 0) {
                                textView.setText(temp.get("voteid").toString());
                                //get id
                            } else {
                                textView.setText(temp.get("votes").toString());
                                //get vote amount
                            }


                            textView.setPadding(20, 20, 20, 20);
                            tableRow.addView(textView);
                        }
                        tableLayout.addView(tableRow);
                    }
                    setContentView(tableLayout);


                    Log.d("votes", ": " + voteshow.size());
                } else {
                    Log.d("votes", "Error: " + e.getMessage());
                }
            }
        });


    }
}