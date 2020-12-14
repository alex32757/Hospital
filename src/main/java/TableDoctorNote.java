import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TableDoctorNote extends Box {
    private final JTable table = new JTable();
    private final String[] columnName = {"doctor_name", "disease", "date"};
    private final ArrayList<String[]> tableList = new ArrayList<>();
    private List<Patient> patientList;
    protected final MyComboBox<Patient> patientMyComboBox = new MyComboBox<>();
    private List<DoctorNote> doctorNoteList;

    public TableDoctorNote() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        JScrollPane tableScroll = new JScrollPane(table);
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);
        table.setShowGrid(true);

        patientMyComboBox.getComboBox().setMaximumSize(new Dimension(300, 30));
        patientMyComboBox.getComboBox().addActionListener(this::actionPerformedChooseCombo);

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGap(10)
                .addComponent(patientMyComboBox.getComboBox())
                .addGap(10)
                .addComponent(tableScroll));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(patientMyComboBox.getComboBox())
                .addComponent(tableScroll));

        add(panel);
    }

    protected void updateComboBox() {
        Hospital.session(session -> patientList = session.createQuery("select a from Patient a",
                Patient.class).getResultList());
        patientMyComboBox.update(patientList);
    }

    public void actionPerformedChooseCombo(ActionEvent e) {
        tableList.clear();
        Patient patient = patientMyComboBox.getSelectedItem();
        if(patient != null) {
            Hospital.session(session -> doctorNoteList = session.createQuery("select d from DoctorNote d where d.patient.id = :id",
                    DoctorNote.class).setParameter("id", patient.getId()).getResultList());
            for (DoctorNote dn : doctorNoteList) {
                String[] str = {dn.getDoctor().toString(), dn.getDisease(), dn.getDate()};
                tableList.add(str);
            }
            TableModel tableModel = new DefaultTableModel(
                    tableList.toArray(new String[0][0]),
                    columnName);
            table.setModel(tableModel);
            Hospital.logger.log(Level.INFO, "Showing a doctor notes for patient " + patientMyComboBox.getSelectedItem().toString());
        }
    }
}
