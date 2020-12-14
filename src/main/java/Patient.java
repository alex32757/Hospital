import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "patient")

public class Patient extends Person implements MyComboBox.GetId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false, insertable = false, updatable = false)
    private int id;

    @OneToMany(mappedBy = "patient", orphanRemoval = true)
    private List<DoctorNote> doctorNotes;

    public Patient() {}

    @Override
    public String toString() {
        return getLastName() + " " + getFirstName();
    }

    public Patient(String firstName, String lastName, String dateOfBirth){
        super(firstName, lastName, dateOfBirth);
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DoctorNote> getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(List<DoctorNote> doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
