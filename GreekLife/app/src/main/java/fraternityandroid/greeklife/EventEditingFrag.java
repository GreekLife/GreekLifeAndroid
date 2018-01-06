package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Jon Zlotnik on 2018-01-05.
 */

public class EventEditingFrag extends DialogFragment {
    public Calendar eventDate = Calendar.getInstance();
    public long duration = 0;
    public String eventTitle = "";
    String location = "" ;
    String description = "";
    public View presentingView;
    boolean creatingNew;
    AlertDialog.Builder builder;
    View edittingView;
    CalendarActivity presentingClass;


    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPresentingView(View view) {
        this.presentingView = view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        creatingNew = presentingView.getId() == R.id.newEventBTN;

        builder = new AlertDialog.Builder(getActivity());
        edittingView = View.inflate(builder.getContext(), R.layout.event_editing, null);

        hideDatePicker(edittingView);
        hideTimePicker(edittingView);
        ((Button) edittingView.findViewById(R.id.eventDateBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edittingView.findViewById(R.id.datePicker).getVisibility() == View.GONE) {
                    hideTimePicker(edittingView);
                    edittingView.findViewById(R.id.datePicker).setVisibility(View.VISIBLE);
                    if (!creatingNew) {
                        ((DatePicker) (edittingView).findViewById(R.id.datePicker)).updateDate(
                                eventDate.get(Calendar.YEAR),
                                eventDate.get(Calendar.MONTH),
                                eventDate.get(Calendar.DAY_OF_MONTH)
                        );
                    }
                } else {
                    hideDatePicker(edittingView);
                }
            }
        });
        ((Button) edittingView.findViewById(R.id.eventTimeBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edittingView.findViewById(R.id.timePicker).getVisibility() == View.GONE) {
                    hideDatePicker(edittingView);
                    edittingView.findViewById(R.id.timePicker).setVisibility(View.VISIBLE);
                    TimePicker timePicker = edittingView.findViewById(R.id.timePicker);
                    if (!creatingNew) {
                        timePicker.setHour(eventDate.get(Calendar.HOUR_OF_DAY));
                        timePicker.setMinute(eventDate.get(Calendar.MINUTE));
                    }
                } else {
                    hideTimePicker(edittingView);
                }

            }
        });
        if (creatingNew) {
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        } else {
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            ((EditText) edittingView.findViewById(R.id.eventTitleField)).setText(eventTitle);
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.setView(edittingView);
        return builder.create();
    }

    public void updateProperties () {
        eventTitle = ((EditText)edittingView.findViewById(R.id.eventTitleField)).getText().toString();
        DatePicker datePicker = (DatePicker)edittingView.findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker)edittingView.findViewById(R.id.timePicker);
        Calendar startDate = Calendar.getInstance();
        startDate.set(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getHour(),
                timePicker.getMinute()
        );
        eventDate = startDate;
        if(((EditText)edittingView.findViewById(R.id.hoursField)).getText().toString().isEmpty() ||
                ((EditText)edittingView.findViewById(R.id.minutesField)).getText().toString().isEmpty()) {
            duration = 0;
        } else {
            long hoursInMillis = 60 * 60 * 1000 * Long.parseLong(((EditText) edittingView.findViewById(R.id.hoursField)).getText().toString());
            long minuteInMillis = 60 * 1000 * Long.parseLong(((EditText) edittingView.findViewById(R.id.minutesField)).getText().toString());
            duration = (hoursInMillis+minuteInMillis)/1000;
        }
        location = ((EditText)edittingView.findViewById(R.id.eventLocationField)).getText().toString();
        description = ((EditText)edittingView.findViewById(R.id.eventDescriptionField)).getText().toString();
    }

    public void hideDatePicker(View view) {
        DatePicker datePicker = view.findViewById(R.id.datePicker);

        String month = CalendarTools.monthToString(datePicker.getMonth());
        String day = String.valueOf(datePicker.getDayOfMonth());
        String year = String.valueOf(datePicker.getYear());

        ((Button) view.findViewById(R.id.eventDateBTN)).setText(month + " " + day + ", " + year);

        view.findViewById(R.id.datePicker).setVisibility(View.GONE);
    }

    public void hideTimePicker(View view) {
        TimePicker timePicker = view.findViewById(R.id.timePicker);

        String hour;
        String amPm;
        if (timePicker.getHour() > 11) {
            hour = String.valueOf(timePicker.getHour() - 12);
            amPm = "pm";
        } else {
            hour = String.valueOf(timePicker.getHour());
            amPm = "am";
        }
        String minute;
        if (timePicker.getMinute() < 10) {
            minute = "0" + String.valueOf(timePicker.getMinute());
        } else {
            minute = String.valueOf(timePicker.getMinute());
        }

        ((Button) view.findViewById(R.id.eventTimeBTN)).setText(hour + ":" + minute + " " + amPm);

        view.findViewById(R.id.timePicker).setVisibility(View.GONE);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        updateProperties();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    updateProperties();
                    Boolean wantToCloseDialog = false;
                     if (eventDate == null
                            || eventTitle.isEmpty()
                            || duration == 0
                            || location.isEmpty()
                            || description.isEmpty())
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Please ensure that no fields are empty.")
                                .setTitle("Empty Fields!")
                                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                         DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                         if(creatingNew){
                             mDatabase = mDatabase.child("Calendar");
                             String eventID = String.valueOf((int)(eventDate.getTimeInMillis()/1000));
                             mDatabase.child(eventID+"/date").setValue(Double.parseDouble(eventID));
                             mDatabase.child(eventID+"/description").setValue(description);
                             mDatabase.child(eventID+"/duration").setValue(duration);
                             mDatabase.child(eventID+"/location").setValue(location);
                             mDatabase.child(eventID+"/title").setValue(eventTitle);
                             mDatabase.child(eventID+"/attendees/"+Globals.getInstance().getLoggedIn().UserID).setValue(Globals.getInstance().getLoggedIn().BrotherName);
                         }
                         wantToCloseDialog = true;
                         presentingClass.reloadUI();
                    }
                    if(wantToCloseDialog)
                        d.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

}
