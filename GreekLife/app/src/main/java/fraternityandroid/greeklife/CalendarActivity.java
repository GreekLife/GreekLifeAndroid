package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity {
    Globals globals = Globals.getInstance();

    //---------------------------------------------------------------
    // The Model
    //
    class CalendarEvent {
        public HashMap<String, String> attendees;
        public long eventDate;
        public String eventDescription;
        public long eventDuration;
        public String eventLocation;
        public String eventTitle;
        public Boolean cancelled;

        CalendarEvent(
                HashMap<String, String> attendees,
                long eventDate,
                String eventDescription,
                long eventDuration,
                String eventLocation,
                String eventTitle,
                Boolean cancelled

        ) {
            this.attendees = attendees;
            this.eventDate = eventDate;
            this.eventDescription = eventDescription;
            this.eventDuration = eventDuration;
            this.eventLocation = eventLocation;
            this.eventTitle = eventTitle;
            this.cancelled = cancelled;
        }

        public String formatTime(Calendar cal) {

            String formattedTime = "";

            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);
            if (hour == 0) {
                hour = 12;
            }
            formattedTime = hour + ":";
            if (minute < 10) {
                formattedTime += "0" + minute;
            } else {
                formattedTime += minute;
            }

            if (cal.get(Calendar.AM_PM) == 1) {
                formattedTime += "pm";
            } else {
                formattedTime += "am";
            }

            return formattedTime;
        }
    }

    class TheCalendar {

        public int monthViewing = Calendar.getInstance().get(Calendar.MONTH);
        public int yearViewing = Calendar.getInstance().get(Calendar.YEAR);

        public ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();

        public void addEvent(DataSnapshot snapshot) {
            HashMap<String, String> attendees = new HashMap<String, String>();
            for (DataSnapshot childSnapshot : snapshot.child("attendees").getChildren()) {
                attendees.put(childSnapshot.getKey(), childSnapshot.getValue().toString());
            }
            long eventDate = ((Number) snapshot.child("date").getValue()).longValue();
            String eventDescription = (String) snapshot.child("description").getValue();
            long eventDuration = ((Number) snapshot.child("duration").getValue()).longValue();
            String eventLocation = (String) snapshot.child("location").getValue();
            String eventTitle = (String) snapshot.child("title").getValue();
            Boolean cancelled = (Boolean) snapshot.child("Cancelled").getValue();
            if(cancelled == null) {
                cancelled = false;
            }
            CalendarEvent newEvent = new CalendarEvent(attendees, eventDate, eventDescription, eventDuration, eventLocation, eventTitle, cancelled);
            calendarEvents.add(newEvent);
        }


    }

    //---------------------------------------------------------------
    // The Controller
    //

    private DatabaseReference mDatabase;
    private TheCalendar theCalendar = new TheCalendar();
    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        globals.IsBlocked(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Globals globals = Globals.getInstance();
        final LinearLayout masterControls = findViewById(R.id.calendarEBoardTools);
        if (!globals.EboardContains(globals.getLoggedIn().Position)) {
            masterControls.removeAllViews();
        }
        reloadUI();
    }

    //--------------------------------
    // Change Month Viewing
    //
    public void goToNextMonth(View view) {
        if (theCalendar.monthViewing == 11) {
            theCalendar.monthViewing = 0;
            theCalendar.yearViewing++;
        } else {
            theCalendar.monthViewing++;
        }
        reloadUI();
    }

    public void goToPrevMonth(View view) {
        if (theCalendar.monthViewing == 0) {
            theCalendar.monthViewing = 11;
            theCalendar.yearViewing--;
        } else {
            theCalendar.monthViewing--;
        }
        reloadUI();
    }

    //-----------------------------------------------------
    //  Reload the UI
    //
    public void reloadUI() {
        final LinearLayout eventListView = findViewById(R.id.eventListView);
        final Button monthButton = findViewById(R.id.monthViewingBTN);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode() + "/Calendar");

        ValueEventListener calendarEventsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (theCalendar.calendarEvents != null) {
                    theCalendar.calendarEvents.clear();
                }
                eventListView.removeAllViewsInLayout();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    theCalendar.addEvent(childSnapshot);
                }
                Calendar prevEvent = Calendar.getInstance();
                prevEvent.setTimeInMillis(0);
                for (CalendarEvent event : theCalendar.calendarEvents) {
                    Calendar currEvent = Calendar.getInstance();
                    currEvent.setTimeInMillis(((long) event.eventDate) * ((long) 1000));
                    if (currEvent.get(Calendar.MONTH) == theCalendar.monthViewing &&
                            currEvent.get(Calendar.YEAR) == theCalendar.yearViewing) {
                        if (CalendarTools.areOnSameDay(prevEvent, currEvent)) {
                            addToDateView(eventListView, currEvent, event);
                        } else {
                            createDateView(eventListView, currEvent, event);
                        }
                        prevEvent = currEvent;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CalendarActivity.this, "There was an ERROR with the Database!!!", Toast.LENGTH_LONG).show();
            }
        };
        mDatabase.addListenerForSingleValueEvent(calendarEventsListener);
        monthButton.setText(CalendarTools.monthToString(theCalendar.monthViewing) + " " + theCalendar.yearViewing);

    }

    //--------------------------------------------------
    // Methods for setting up the event cards/views
    //
    public void createDateView(LinearLayout parent, Calendar date, final CalendarEvent calEvent) {
        View dayLayout = getLayoutInflater().inflate(R.layout.calendar_day_cell, null);

        ((TextView) dayLayout.findViewById(R.id.numDate)).setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
        ((TextView) dayLayout.findViewById(R.id.weekDay)).setText(CalendarTools.weekDayToString(date.get(Calendar.DAY_OF_WEEK)));
        LinearLayout eventsLayout = (dayLayout.findViewById(R.id.eventsLayout));
        eventsLayout.setId(date.get(Calendar.DAY_OF_MONTH));
        View eventLayout = getLayoutInflater().inflate(R.layout.calendar_event_cell, eventsLayout);

        if(globals.EboardContains(globals.getLoggedIn().Position)) {
            ((Button)eventLayout.findViewById(R.id.cancelBTN)).setVisibility(View.VISIBLE);
        }
        else {
            ((Button)eventLayout.findViewById(R.id.cancelBTN)).setVisibility(View.GONE);
        }

        if (editing) {
            eventLayout.findViewById(R.id.deleteBTN).setVisibility(View.VISIBLE);
            ((Button)eventLayout.findViewById(R.id.deleteBTN)).setText("Delete");
        }
        else {
            if(calEvent.cancelled) {
                eventLayout.findViewById(R.id.deleteBTN).setVisibility(View.VISIBLE);
                ((Button)eventLayout.findViewById(R.id.deleteBTN)).setText("Cancelled");
                Object bool = "true";
                eventLayout.findViewById(R.id.cancelBTN).setTag(bool);
            }
            else {
                eventLayout.findViewById(R.id.deleteBTN).setVisibility(View.GONE);
                Object bool = "false";
                eventLayout.findViewById(R.id.cancelBTN).setTag(bool);
            }
        }
        final int idValue = ((Long) (calEvent.eventDate)).intValue();
        eventLayout.findViewById(R.id.deleteBTN).setId(idValue);
        ((TextView) eventLayout.findViewById(R.id.eventTitle)).setText(calEvent.eventTitle);
        ((TextView) eventLayout.findViewById(R.id.eventStartTime)).setText(
                calEvent.formatTime(date)
        );
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(date.getTimeInMillis() + (long) calEvent.eventDuration * 1000);
        ((TextView) eventLayout.findViewById(R.id.eventEndTime)).setText(
                calEvent.formatTime(endTime)
        );
        ((TextView) eventLayout.findViewById(R.id.eventLocation)).setText(
                calEvent.eventLocation
        );
        ((TextView) eventLayout.findViewById(R.id.eventDescription)).setText(
                calEvent.eventDescription
        );

        if( calEvent.attendees.containsKey(globals.getLoggedIn().UserID)){
            ((ToggleButton)eventLayout.findViewById(R.id.goingNotGoing)).setChecked(true);
        }else {
            ((ToggleButton)eventLayout.findViewById(R.id.goingNotGoing)).setChecked(false);
        }
        ((ToggleButton)eventLayout.findViewById(R.id.goingNotGoing)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean going) {
                if (going){
                    FirebaseDatabase.getInstance().getReference()
                            .child(globals.DatabaseNode()+"/Calendar/"+idValue+"/attendees/"+globals.getLoggedIn().UserID).setValue(globals.getLoggedIn().First_Name + " " + globals.getLoggedIn().Last_Name);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child(globals.DatabaseNode()+"/Calendar/"+idValue+"/attendees/"+globals.getLoggedIn().UserID).removeValue();
                }
                reloadUI();
            }
        });


        ArrayList<String> attendanceList = new ArrayList<>();
        for (String userID:(calEvent.attendees.keySet())){
            attendanceList.add(globals.getUserByID(userID).First_Name+" "+globals.getUserByID(userID).Last_Name);
        }
         final String[] finalAttendanceList = attendanceList.toArray(new String[attendanceList.size()]);
        ((Button)eventLayout.findViewById(R.id.whosGoing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                builder.setTitle("Attendance:").setItems(finalAttendanceList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        eventLayout.findViewById(R.id.cancelBTN).setId(idValue);


        parent.addView(dayLayout);
    }

    public void addToDateView(LinearLayout parent, Calendar date, CalendarEvent calEvent) {
        LinearLayout eventsLayout = parent.findViewById(date.get(Calendar.DAY_OF_MONTH));
        View eventLayout = getLayoutInflater().inflate(R.layout.calendar_event_cell, null);
        ((TextView) eventLayout.findViewById(R.id.eventTitle)).setText(calEvent.eventTitle);
        ((TextView) eventLayout.findViewById(R.id.eventStartTime)).setText(
                calEvent.formatTime(date)
        );
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(date.getTimeInMillis() + (long) calEvent.eventDuration * 1000);
        ((TextView) eventLayout.findViewById(R.id.eventEndTime)).setText(
                calEvent.formatTime(endTime)
        );
        ((TextView) eventLayout.findViewById(R.id.eventLocation)).setText(
                calEvent.eventLocation
        );
        ((TextView) eventLayout.findViewById(R.id.eventDescription)).setText(
                calEvent.eventDescription
        );
        if(globals.EboardContains(globals.getLoggedIn().Position)) {
            ((Button)eventLayout.findViewById(R.id.cancelBTN)).setVisibility(View.VISIBLE);
        }
        else {
            ((Button)eventLayout.findViewById(R.id.cancelBTN)).setVisibility(View.GONE);
        }
        if (editing) {
            eventLayout.findViewById(R.id.deleteBTN).setVisibility(View.VISIBLE);
            ((Button)eventLayout.findViewById(R.id.deleteBTN)).setText("Delete");
        }
        else {
            if(calEvent.cancelled) {
                eventLayout.findViewById(R.id.deleteBTN).setVisibility(View.VISIBLE);
                ((Button)eventLayout.findViewById(R.id.deleteBTN)).setText("Cancelled");
                Object bool = "true";
                eventLayout.findViewById(R.id.cancelBTN).setTag(bool);
            }
            else {
                eventLayout.findViewById(R.id.deleteBTN).setVisibility(View.GONE);
                Object bool = "false";
                eventLayout.findViewById(R.id.cancelBTN).setTag(bool);
            }
        }
        final int idValue = ((Long) (calEvent.eventDate)).intValue();

        if( calEvent.attendees.containsKey(globals.getLoggedIn().UserID)){
            ((ToggleButton)eventLayout.findViewById(R.id.goingNotGoing)).setChecked(true);
        }else {
            ((ToggleButton)eventLayout.findViewById(R.id.goingNotGoing)).setChecked(false);
        }
        ((ToggleButton)eventLayout.findViewById(R.id.goingNotGoing)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean going) {
                if (going){
                    FirebaseDatabase.getInstance().getReference()
                            .child(globals.DatabaseNode()+"/Calendar/"+idValue+"/attendees/"+globals.getLoggedIn().UserID).setValue(globals.getLoggedIn().UserID);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child(globals.DatabaseNode()+"/Calendar/"+idValue+"/attendees/"+globals.getLoggedIn().UserID).removeValue();
                }
                reloadUI();
            }
        });
        String attendanceList = "";
        for (String userID:(calEvent.attendees.keySet())){
            attendanceList +=
                    " - "+globals.getUserByID(userID).First_Name+" "+globals.getUserByID(userID).Last_Name+"\n";
        }
        final String finalAttendanceList = attendanceList;
        ((Button)eventLayout.findViewById(R.id.whosGoing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                builder.setTitle("Attendance:").setMessage(finalAttendanceList);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        eventLayout.findViewById(R.id.deleteBTN).setId(idValue);
        eventLayout.findViewById(R.id.cancelBTN).setId(idValue);

        eventsLayout.addView(eventLayout);

    }

    //---------------------------------
    //  Editing/creating an event
    //
    public void editEvent(View view) {
        EventEditingFrag editEventFrag = new EventEditingFrag();
        editEventFrag.presentingView = view;
        editEventFrag.presentingClass = this;
        editEventFrag.show(getFragmentManager(), "eventEdit");
    }

    public void editMode(View view) {
        if (editing) {
            editing = false;
        } else {
            editing = true;
        }
        reloadUI();
    }

    //------------------------------------
    //  cancel an event
    //

    public void cancelEvent(View view) {

        final View viewy = view;
        if(viewy.getTag() == "false") {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(globals.DatabaseNode() + "/Calendar/" + viewy.getId() + "/Cancelled").setValue(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The event has been cancelled").setTitle("Cancelled");
            builder.show();

        }
        else if(viewy.getTag() == "true") {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(globals.DatabaseNode() + "/Calendar/" + viewy.getId() + "/Cancelled").setValue(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The event has been uncancelled").setTitle("Uncancelled");
            builder.show();
        }

        reloadUI();
    }

    //------------------------------------
    //  Deleting an event
    //
    public void deleteEvent(View view) {

        if(editing) {
            final View viewy = view;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You sure you wanna delete that, bro?").setTitle("Confirm Delete");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(globals.DatabaseNode() + "/Calendar/" + viewy.getId()).removeValue();
                    reloadUI();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}