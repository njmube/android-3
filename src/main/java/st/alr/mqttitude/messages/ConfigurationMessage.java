package st.alr.mqttitude.messages;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumSet;

import st.alr.mqttitude.App;
import st.alr.mqttitude.R;
import st.alr.mqttitude.db.Waypoint;
import st.alr.mqttitude.support.Preferences;
import st.alr.mqttitude.support.StringifiedJSONObject;

public class ConfigurationMessage {
    StringifiedJSONObject json;
    public static enum Includes {CONNECTION, CREDENTIALS, IDENTIFICATION, WAYPOINTS}

    public ConfigurationMessage(EnumSet<Includes> includes) {
        json = Preferences.toJSONObject();

        if(!includes.contains(Includes.CONNECTION))
            stripConnection();

        if(!includes.contains(Includes.CREDENTIALS))
            stripCredentials();

        if(!includes.contains(Includes.IDENTIFICATION))
            stripDeviceIdentification();

        if(includes.contains(Includes.WAYPOINTS))
            try {
                json.put("waypoints", getWaypointJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }


    private StringifiedJSONObject getPreferencesJson() {
        return Preferences.toJSONObject();
    }
    private JSONArray getWaypointJson() {
        StringifiedJSONObject j = new StringifiedJSONObject();

        JSONArray waypoints = new JSONArray();
        for(Waypoint waypoint : App.getWaypointDao().loadAll()) {
            WaypointMessage wpM = new WaypointMessage(waypoint);
            wpM.setTrackerId(Preferences.getTrackerId());
            StringifiedJSONObject wp = wpM.toJSONObject();
            try { wp.put("shared", waypoint.getShared()); } catch (JSONException e) { }
            try { wp.put("transition", waypoint.getTransitionType()); } catch (JSONException e) { }
            waypoints.put(wp);
        }
        return waypoints;
    }

    public String toString() {return toJSONObject().toString(); }
    public StringifiedJSONObject toJSONObject() {
        return json;
    }

    public ConfigurationMessage stripCredentials() {
        json.remove(Preferences.getStringRessource(R.string.keyUsername));
        json.remove(Preferences.getStringRessource(R.string.keyPassword));
        return this;
    }

    public ConfigurationMessage stripDeviceIdentification() {
        json.remove(Preferences.getStringRessource(R.string.keyDeviceId));
        json.remove(Preferences.getStringRessource(R.string.keyClientId));
        json.remove(Preferences.getStringRessource(R.string.keyTrackerId));
        return this;
    }

    public ConfigurationMessage stripConnection() {
        json.remove(Preferences.getStringRessource(R.string.keyHost));
        json.remove(Preferences.getStringRessource(R.string.keyPort));
        json.remove(Preferences.getStringRessource(R.string.keyAuth));
        json.remove(Preferences.getStringRessource(R.string.keyTls));
        json.remove(Preferences.getStringRessource(R.string.keyTlsCrtPath));
        json.remove(Preferences.getStringRessource(R.string.keyConnectionAdvancedMode));
        json.remove(Preferences.getStringRessource(R.string.keyCleanSession));
        return this;
    }

    public ConfigurationMessage stripWaypoints() {
        json.remove("waypoints");
        return this;
    }

    public void remove(String key) {
        json.remove(key);
    }

}
