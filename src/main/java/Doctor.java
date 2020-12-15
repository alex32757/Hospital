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

    public int getId() {
        return id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setSchedule(List<WorkTime> schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return getLastName() + " " + getFirstName();
    }
}
