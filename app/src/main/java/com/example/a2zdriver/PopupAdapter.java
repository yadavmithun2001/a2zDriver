package com.example.a2zdriver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.security.auth.callback.Callback;

public class PopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
    private HashMap<String, Uri> images=null;
    private Context ctxt=null;
    private int iconWidth=-1;
    private int iconHeight=-1;
    private Marker lastMarker=null;

    PopupAdapter(Context ctxt, LayoutInflater inflater,
                 HashMap<String, Uri> images) {
        this.ctxt=ctxt;
        this.inflater=inflater;
        this.images=images;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.popup, null);
        }

        if (lastMarker == null
                || !lastMarker.getId().equals(marker.getId())) {
            lastMarker=marker;
            TextView date=(TextView)popup.findViewById(R.id.date);
            TextView time=(TextView)popup.findViewById(R.id.time);
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
            Date currentLocalTime = cal.getTime();
            DateFormat date1 = new SimpleDateFormat("HH:mm");
            date1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String localTime = date1.format(currentLocalTime);
            time.setText(localTime);
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String crdate = df.format(Calendar.getInstance().getTime());
            date.setText(crdate);


            Uri image=images.get(marker.getId());
        }

        return(popup);
    }

    static class MarkerCallback implements Callback {
        Marker marker=null;

        MarkerCallback(Marker marker) {
            this.marker=marker;
        }


    }

}
