package com.example.steff.sharedeffort;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FamilyBoardActivity extends AppCompatActivity {

    private Spinner mUserSpinner;
    private TextView mUserPoints;
    private ImageButton mPreviousWeekBtn;
    private ImageButton mNextWeekBtn;
    private ImageButton mAddBtn;
    private ImageButton mLogoutBtn;

    private List<Task> mTaskList;
    private List<Event> mEventList;

    private Map<String, LocalDate> mCurrentWeek;
    private DateHandler mDateHandler;

    private List<Task> mFreeTasks;
    private HashMap<Integer, List<Task>> mPersonTasks;
    private HashMap<Integer, List<Event>> mPersonEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_board);
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyBoardActivity.this);
            toaster.showToast();
        }

        mUserPoints = findViewById(R.id.activity_familyBoard_points);
        mUserPoints.setText(ConnectedUserInfo.getInstance().getConnectedMember().getFname() + " : " + ConnectedUserInfo.getInstance().getConnectedMember().getPoints());
        mDateHandler = new DateHandler();
        mCurrentWeek = mDateHandler.getWeekDates();
        fillDates();
        createSpinner();
        mPreviousWeekBtn = findViewById(R.id.activity_familyBoard_previous_week_btn);
        mNextWeekBtn = findViewById(R.id.activity_familyBoard_next_week_btn);
        mAddBtn = findViewById(R.id.activity_familyBoard_add_btn);
        mLogoutBtn = findViewById(R.id.activity_familyBoard_logout_btn);

        FamilyMember famMember = ConnectedUserInfo.getInstance().getConnectedMember();


        mPreviousWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPreviousWeek();
            }
        });

        mNextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextWeek();
            }
        });

        getTasksAndEvents();
        initAddBtn();
        initLogoutBtn();
    }

    public void onRestart() {
        super.onRestart();
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyBoardActivity.this);
            toaster.showToast();
        }
        getTasksAndEvents();

    }
    public void fillDates(){
        String[] keys = mDateHandler.getDayNames();
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
            String localDate = mCurrentWeek.get(keys[i]).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH));
            textView.setText(localDate);
        }
    }

    public void createSpinner(){
        List<String> familyMembersName  = new ArrayList<>();
        List<FamilyMember> familyMembers = ConnectedUserInfo.getInstance().getFamilyMembers();

        familyMembersName.add("Voir tout");
        familyMembersName.add("Voir tâches libres");

        for(int i = 0; i < familyMembers.size(); i++){
            familyMembersName.add(familyMembers.get(i).getFname());
        }

        mUserSpinner = findViewById(R.id.activity_familyBoard_userSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, familyMembersName);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mUserSpinner.setAdapter(adapter);
        mUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               reactToSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getTasksAndEvents(){
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
                JSONArray events = null;
                try {
                    tasks = jsonObject.getJSONArray("tasks");
                    events = jsonObject.getJSONArray("events");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(tasks != null && events != null){
                    mTaskList = new ArrayList<>();
                    mEventList = new ArrayList<>();
                    for(int i = 0 ; i < tasks.length(); i++){
                        try {
                            JSONObject taskObject = (JSONObject) tasks.get(i);
                            Task task = new Task(
                                    taskObject.getInt("taskId"),
                                    taskObject.getString("taskName"),
                                    taskObject.getInt("points"),
                                    taskObject.getInt("tPoints"),
                                    taskObject.getInt("isRecu"),
                                    taskObject.getInt("persId"),
                                    taskObject.getString("taskDate"),
                                    taskObject.getInt("status")
                            );
                            mTaskList.add(task);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    for(int i = 0 ; i < events.length(); i++){
                        try {
                            JSONObject eventObject = (JSONObject) events.get(i);
                            Event event = new Event(
                                    eventObject.getInt("eventId"),
                                    eventObject.getString("eventName"),
                                    eventObject.getString("eventDesc"),
                                    eventObject.getInt("persId"),
                                    eventObject.getInt("isRecu"),
                                    eventObject.getString("eventDate")
                            );
                            mEventList.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            filterTasks();
                        }
                    });
                }
            }
        };
        new Thread(new ApiRequestHandler("board/getTasksAndEvents", tasksRequest, eventNotifier)).start();
    }

    public void filterTasks(){
        mFreeTasks = new ArrayList<>();
        mPersonTasks = new HashMap<>();
        mPersonEvents = new HashMap<>();

        for(int i = 0; i < mTaskList.size(); i++){
            Task task = mTaskList.get(i);
            if(task.personId == -1){
                mFreeTasks.add(task);
            }
            else {
                List<Task> taskList;
                if(mPersonTasks.containsKey(task.personId)){
                    taskList = mPersonTasks.get(task.personId);
                }
                else{
                    taskList = new ArrayList<>();
                }
                taskList.add(task);
                mPersonTasks.put(task.personId, taskList);
            }
        }

        for(int i = 0; i < mEventList.size(); i++){
            Event event = mEventList.get(i);
            if(event.personId != -1){
                List<Event> eventList;
                if(mPersonEvents.containsKey(event.personId)){
                    eventList = mPersonEvents.get(event.personId);
                }
                else{
                    eventList = new ArrayList<>();
                }
                eventList.add(event);
                mPersonEvents.put(event.personId, eventList);
            }
        }
        reactToSpinner(mUserSpinner.getSelectedItemPosition());
    }

    public void reactToSpinner(int position){
        if(position == 0){
            createClickableAffairs(mTaskList, mEventList);
        }
        else if(position == 1) {
            createClickableAffairs(mFreeTasks, null);
        }
        else{
            int persId = ConnectedUserInfo.getInstance().getFamilyMembers().get(position - 2).getId();
            createClickableAffairs(mPersonTasks.get(persId), mPersonEvents.get(persId));
        }
    }

    public void createClickableAffairs(List<Task> taskList, List<Event> eventList){
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
        if(taskList != null) {
            for(int i = 0; i < taskList.size(); i++){
                final Task task = taskList.get(i);
                if(mCurrentWeek.containsValue(task.taskDate.toLocalDate())){
                    LinearLayout layout = findViewById(taskLayoutIds[task.taskDate.getDayOfWeek().getValue()-1]);
                    Button btn = new Button(this);
                    btn.setText(task.taskName);

                    btn.setScaleX(0.8F);
                    btn.setScaleY(0.8F);
                    btn.setAllCaps(false);
                    btn.setTextSize(19);
                    btn.setTextColor(getResources().getColor(R.color.textColor));

                    if(task.status == 0) {
                        btn.setBackgroundColor(getResources().getColor(R.color.toDoTaskColor));

                    }
                    if(task.status == 1) {
                        btn.setBackgroundColor(getResources().getColor(R.color.toValidateTaskColor));
                    }
                    if(task.status == 2){
                        btn.setBackgroundColor(getResources().getColor(R.color.doneTaskColor));
                    }


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
        }

        if(eventList != null){
            for(int i = 0; i < eventList.size(); i++){
                final Event event = eventList.get(i);
                if(mCurrentWeek.containsValue(event.eventDate.toLocalDate())){
                    LinearLayout layout = findViewById(taskLayoutIds[event.eventDate.getDayOfWeek().getValue()-1]);

                    Button btn = new Button(this);
                    btn.setText(event.eventName);
                    //btn.setBackgroundColor(getResources().getColor(R.color.eventButtonColor));
                    btn.setScaleX(0.8F);
                    btn.setScaleY(0.8F);
                    btn.setBackgroundColor(getResources().getColor(R.color.logoBottom));
                    btn.setTextColor(getResources().getColor(R.color.logoMiddleBottom));
                    btn.setTextSize(20);
                    btn.setAllCaps(false);
                    btn.setPadding(20, 20, 20, 20);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent eventDetailActivity = new Intent(FamilyBoardActivity.this, EventDetailActivity.class);
                            eventDetailActivity.putExtra("event", event);
                            startActivity(eventDetailActivity);
                        }
                    });
                    layout.addView(btn);
                }
            }
        }
    }

    private void initAddBtn(){
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddChoiceDialog();
            }
        });
    }

    public void openAddChoiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_choice_dialog, null);
        builder.setView(dialogView);
        Button task = dialogView.findViewById(R.id.add_task_btn);
        if(ConnectedUserInfo.getInstance().getConnectedMember().getAdmin()){
            task.setVisibility(View.VISIBLE);
        }
        final AlertDialog alertDialog = builder.create();
        Button event = dialogView.findViewById(R.id.add_event_btn);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                addEvent();
            }
        });
        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                addTask();
            }
        });
        alertDialog.show();
    }

    public void initLogoutBtn(){
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent familyMemberManagementActivity = new Intent(FamilyBoardActivity.this, FamilyMemberManagementActivity.class);
                startActivity(familyMemberManagementActivity);
            }
        });
    }

    public void goToNextWeek(){
        mCurrentWeek = mDateHandler.getNextWeek(mCurrentWeek);
        fillDates();
        createClickableAffairs(mTaskList, mEventList);
        reactToSpinner(mUserSpinner.getSelectedItemPosition());
    }
    public void goToPreviousWeek(){
        mCurrentWeek = mDateHandler.getPreviousWeek(mCurrentWeek);
        fillDates();
        createClickableAffairs(mTaskList, mEventList);
        reactToSpinner(mUserSpinner.getSelectedItemPosition());
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
