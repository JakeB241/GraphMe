package umich.jakebock.trackme.classes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jake on 1/22/2018.
 */

public class Setting implements Serializable
{
    public enum SettingType {SWITCH, SPINNER};

    private String          settingId;
    private String          labelText;
    private SettingType     settingType;
    private Object          defaultValue;
    private List<String>    availableValues;

    private Object      chosenValue;

    public Setting() {}

    public Setting(String settingId, String labelText, SettingType settingType, Object defaultValue, List<String> availableValues)
    {
        this.settingId       = settingId;
        this.labelText       = labelText;
        this.settingType     = settingType;
        this.defaultValue    = defaultValue;
        this.availableValues = availableValues;
    }

    // Getters/Settings
    public String getSettingId() {
        return settingId;
    }

    public String getLabelText() {
        return labelText;
    }

    public SettingType getSettingType() {
        return settingType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public List<String> getAvailableValues() {
        return availableValues;
    }

    public Object getChosenValue() {
        return chosenValue;
    }

    public void setChosenValue(Object chosenValue) {
        this.chosenValue = chosenValue;
    }
}
