import javax.persistence.*;

@Entity
@Table(name = "doctorNote")

public class DoctorNote implements MyComboBox.GetId {
    public DoctorNote() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false, insertable = false, updatable = false)
    private int id;

    @Column(name = "disease")
    private String disease;

    @Column(name = "date")
    private String date;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    public DoctorNote(Patient patient, Doctor doctor, String date, String disease) {
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.disease = disease;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String specialization) {
        this.disease = specialization;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
