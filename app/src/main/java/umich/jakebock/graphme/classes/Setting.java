package umich.jakebock.graphme.classes;

/**
 * Created by Jake on 1/22/2018.
 */

public class Setting
{
    public enum SettingType {CHECKBOX, COMBOBOX};

    private String      labelText;
    private SettingType settingType;
    private Object      defaultValue;
    private Object[]    availableValues;

    private Object      chosenValue;

    public Setting(String labelText, SettingType settingType, Object defaultValue)
    {
        this.labelText    = labelText;
        this.settingType  = settingType;
        this.defaultValue = defaultValue;
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

    public Object[] getAvailableValues() {
        return availableValues;
    }

    public Object getChosenValue() {
        return chosenValue;
    }

    public void setChosenValue(Object chosenValue) {
        this.chosenValue = chosenValue;
    }




}
