package com.example.steff.sharedeffort;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FamilyBoardActivity extends AppCompatActivity {

    private Spinner mUserSpinner;

    private Button previousWeekBtn;

    private Button nextWeekBtn;

    private Button addTaskBtn;

    private Button addEventBtn;

    private List<Task> taskList;

    private List<Event> eventList;

    private Map<String, LocalDate> currentWeek;

    private DateHandler dateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_board);
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyBoardActivity.this);
            toaster.showToast();
        }

        dateHandler = new DateHandler();
        currentWeek = dateHandler.getWeekDates();
        fillDates();
        getFamilyInfo();

        previousWeekBtn = findViewById(R.id.activity_familyBoard_previous_week_btn);
        nextWeekBtn = findViewById(R.id.activity_familyBoard_next_week_btn);
        addTaskBtn = findViewById(R.id.activity_familyBoard_addTask_btn);
        addEventBtn = findViewById(R.id.activity_familyBoard_addEvent_btn);

        previousWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPreviousWeek();
            }
        });

        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextWeek();
            }
        });

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });
    }

    public void onRestart() {
        super.onRestart();
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyBoardActivity.this);
            toaster.showToast();
        }
        getFamilyInfo();
        fillDates();
    }
    public void fillDates(){
        String[] keys = dateHandler.getDayNames();
        int[] dateViewIds = new int[] {
                R.id.activity_familyBoard_monday_text,
                R.id.activity_familyBoard_tuesday_text,
                R.id.activity_familyBoard_wednesday_text,
                R.id.activity_familyBoard_thursday_text,
                R.id.activity_familyBoard_friday_text,
                R.id.activity_familyBoard_saturday_text,
                R.id.activity_familyBoard_sunday_text
        };
        for(int i = 0; i < keys.length; i++){
            TextView textView = findViewById(dateViewIds[i]);
            String localDate = currentWeek.get(keys[i]).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH));
            textView.setText(localDate);
        }
    }

    public void createSpinner(List<String> familyMembersName){
        mUserSpinner = findViewById(R.id.activity_familyBoard_userSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, familyMembersName);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mUserSpinner.setAdapter(adapter);
        mUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                if(position == 0){
                    getAllTasks();
                }
                else{
                    getPersonTasks();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getPersonTasks();
    }

    public void getAllTasks(){
        JSONObject tasksRequest = new JSONObject();
        try {
            tasksRequest.put("familyId", ConnectedUserInfo.getInstance().getFamilyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray tasks = null;
                try {
                    tasks = jsonObject.getJSONArray("tasks");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(tasks != null){
                    taskList = new ArrayList<>();
                    for(int i = 0 ; i < tasks.length(); i++){
                        try {
                            JSONObject taskObject = (JSONObject) tasks.get(i);
                            Task task = new Task(
                                    taskObject.getInt("id"),
                                    taskObject.getString("nomTache"),
                                    taskObject.getInt("nbPointsTache"),
                                    taskObject.getInt("nbPointsTransfert"),
                                    taskObject.getString("dateTache"),
                                    taskObject.getInt("estFaite"),
                                    taskObject.getInt("idPersonne"),
                                    taskObject.getInt("estRecurrente")
                            );
                            taskList.add(task);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAllEvents();
                        }
                    });

                }

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/allTasks", tasksRequest, eventNotifier)).start();
    }

    public void getAllEvents(){
        JSONObject tasksRequest = new JSONObject();
        try {
            tasksRequest.put("familyId", ConnectedUserInfo.getInstance().getFamilyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray events = null;
                try {
                    events = jsonObject.getJSONArray("events");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(events != null){
                    eventList = new ArrayList<>();
                    for(int i = 0 ; i < events.length(); i++){
                        try {
                            JSONObject eventObject = (JSONObject) events.get(i);
                            Event event = new Event(
                                    eventObject.getInt("id"),
                                    eventObject.getString("nomEvenement"),
                                    eventObject.getString("descriptionEvenement"),
                                    eventObject.getInt("idPersonne"),
                                    eventObject.getInt("estRecurrent"),
                                    eventObject.getString("dateEvenement")
                            );
                            eventList.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createClickableAffairs();
                        }
                    });

                }

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/allEvents", tasksRequest, eventNotifier)).start();
    }

    public void getPersonTasks(){

        JSONObject tasksRequest = new JSONObject();
        try {
            tasksRequest.put("familyId", ConnectedUserInfo.getInstance().getFamilyId());
            tasksRequest.put("fName",mUserSpinner.getSelectedItem());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray tasks = null;
                try {
                    tasks = jsonObject.getJSONArray("personTasks");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(tasks != null){
                    taskList = new ArrayList<>();
                    for(int i = 0 ; i < tasks.length(); i++){
                        try {
                            JSONObject taskObject = (JSONObject) tasks.get(i);
                            Task task = new Task(
                                    taskObject.getInt("id"),
                                    taskObject.getString("nomTache"),
                                    taskObject.getInt("nbPointsTache"),
                                    taskObject.getInt("nbPointsTransfert"),
                                    taskObject.getString("dateTache"),
                                    taskObject.getInt("estFaite"),
                                    taskObject.getInt("idPersonne"),
                                    taskObject.getInt("estRecurrente")
                            );
                            taskList.add(task);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getPersonEvents();
                        }
                    });

                }

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/personTasks", tasksRequest, eventNotifier)).start();

    }

    public void getPersonEvents() {

        JSONObject eventsRequest = new JSONObject();
        try {
            eventsRequest.put("familyId", ConnectedUserInfo.getInstance().getFamilyId());
            eventsRequest.put("fName", mUserSpinner.getSelectedItem());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray events = null;
                try {
                    events = jsonObject.getJSONArray("personEvents");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (events != null) {
                    eventList = new ArrayList<>();
                    for (int i = 0; i < events.length(); i++) {
                        try {
                            JSONObject eventObject = (JSONObject) events.get(i);
                            Event event = new Event(
                                    eventObject.getInt("id"),
                                    eventObject.getString("nomEvenement"),
                                    eventObject.getString("descriptionEvenement"),
                                    eventObject.getInt("idPersonne"),
                                    eventObject.getInt("estRecurrent"),
                                    eventObject.getString("dateEvenement")
                            );
                            eventList.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createClickableAffairs();
                        }
                    });

                }

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/personEvents", eventsRequest, eventNotifier)).start();
    }

    public void createClickableAffairs(){
        int[] taskLayoutIds = new int[] {
                R.id.activity_familyBoard_monday,
                R.id.activity_familyBoard_tuesday,
                R.id.activity_familyBoard_wednesday,
                R.id.activity_familyBoard_thursday,
                R.id.activity_familyBoard_friday,
                R.id.activity_familyBoard_saturday,
                R.id.activity_familyBoard_sunday
        };

        for(int i = 0; i < taskLayoutIds.length; i++){
            LinearLayout layout = findViewById(taskLayoutIds[i]);
            layout.removeAllViews();
        }

        for(int i = 0; i < taskList.size(); i++){
            final Task task = taskList.get(i);
            if(currentWeek.containsValue(task.taskDate)){
                LinearLayout layout = findViewById(taskLayoutIds[task.taskDate.getDayOfWeek().getValue()-1]);
                Button btn = new Button(this);
                btn.setText(task.taskName);


                btn.setBackgroundColor(getResources().getColor(R.color.taskButtonColor));
                btn.setScaleX(0.8F);
                btn.setScaleY(0.8F);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent taskDetailActivity = new Intent(FamilyBoardActivity.this, TaskDetailActivity.class);
                        taskDetailActivity.putExtra("task", task);
                        startActivity(taskDetailActivity);
                    }
                });
                layout.addView(btn);
            }
        }

        for(int i = 0; i < eventList.size(); i++){
            final Event event = eventList.get(i);
            if(currentWeek.containsValue(event.eventDate)){
                LinearLayout layout = findViewById(taskLayoutIds[event.eventDate.getDayOfWeek().getValue()-1]);
                Button btn = new Button(this);
                btn.setText(event.eventName);
                btn.setBackgroundColor(getResources().getColor(R.color.eventButtonColor));
                btn.setScaleX(0.8F);
                btn.setScaleY(0.8F);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent eventDetailActivity = new Intent(FamilyBoardActivity.this, TaskDetailActivity.class);
                        eventDetailActivity.putExtra("event", event);
                        startActivity(eventDetailActivity);
                    }
                });
                layout.addView(btn);
            }
        }
    }

    public void getFamilyInfo(){
        JSONObject familyId = new JSONObject();
        try {
            familyId.put("familyId", ConnectedUserInfo.getInstance().getFamilyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // to modify
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray familyMembers = null;
                try {
                    familyMembers = jsonObject.getJSONArray("familyMembers");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final List<String> familyMemberNames = new ArrayList<>();
                familyMemberNames.add("Voir tout");
                for(int i = 0; i < familyMembers.length(); i++){
                    try {
                        JSONObject familyMember = (JSONObject) familyMembers.get(i); 
                        familyMemberNames.add(familyMember.getString("fname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        createSpinner(familyMemberNames);
                    }
                });

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/familyInfo", familyId, eventNotifier)).start();
    }

    public void goToNextWeek(){
        currentWeek = dateHandler.getNextWeek(currentWeek);
        fillDates();
        createClickableAffairs();
    }
    public void goToPreviousWeek(){
        currentWeek = dateHandler.getPreviousWeek(currentWeek);
        fillDates();
        createClickableAffairs();
    }

    public void addTask(){
        Intent addTaskActivity = new Intent(FamilyBoardActivity.this, AddTaskActivity.class);
        startActivity(addTaskActivity);
    }

    public void addEvent() {
        Intent addEventActivity = new Intent(FamilyBoardActivity.this, AddEventActivity.class);
        startActivity(addEventActivity);
    }

}
