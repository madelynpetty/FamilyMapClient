package Utils;

public class Settings {
    private static Settings instance = null;
    public boolean lifeStoryLines = true;
    public boolean familyTreeLines = true;
    public boolean spouseLines = true;
    public boolean fathersSide = true;
    public boolean mothersSide = true;
    public boolean maleEvents = true;
    public boolean femaleEvents = true;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
}
