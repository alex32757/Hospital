import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "doctor")
public class Doctor extends Person implements MyComboBox.GetId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false, insertable = false, updatable = false)
    private int id;

    @Column(name = "specialization")
    private String specialization;

    @OneToMany(mappedBy = "doctor", orphanRemoval = true)
    private List<WorkTime> schedule;

    @OneToMany(mappedBy = "doctor", orphanRemoval = true)
    private List<DoctorNote> doctorNotes;

    public Doctor() {}

    public Doctor(String firstName, String lastName, String dateOfBirth, String specialization) {
        super(firstName, lastName, dateOfBirth);
        this.specialization = specialization;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<WorkTime> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<WorkTime> schedule) {
        this.schedule = schedule;
    }

    public List<DoctorNote> getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(List<DoctorNote> doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    @Override
    public String toString() {
        return getLastName() + " " + getFirstName();
    }
}
