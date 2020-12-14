import javax.persistence.*;

@Entity
@Table(name = "worktime")
public class WorkTime {
    public WorkTime() {}

    public WorkTime(Doctor doctor, int dayOfWeek, String startTime, String endTime, int cabNumber) {
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = "";
        this.cabNumber = cabNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Column(name = "activity", nullable = false)
    private String activity;

    @Column(name = "cab_number", nullable = false)
    private int cabNumber;


    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setCabNumber(int cabNumber) {
        this.cabNumber = cabNumber;
    }

    public int getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getActivity() {
        return activity;
    }

    public int getCabNumber() {
        return cabNumber;
    }
}
