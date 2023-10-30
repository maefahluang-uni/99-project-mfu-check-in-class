package th.mfu.model;

public class Section {
    private String TeachingType; // "LAB", "LECT 1", "LECT 2"
    private String Location;
    private String DayOfTheWeek;
    private String Period;
    
    public String getTeachingType() {
        return TeachingType;
    }
    public String getLocation() {
        return Location;
    }
    public void setLocation(String location) {
        Location = location;
    }
    public String getDayOfTheWeek() {
        return DayOfTheWeek;
    }
    public void setDayOfTheWeek(String week) {
        DayOfTheWeek = week;
    }
    public String getPeriod() {
        return Period;
    }
    public void setPeriod(String period) {
        Period = period;
    }
}
