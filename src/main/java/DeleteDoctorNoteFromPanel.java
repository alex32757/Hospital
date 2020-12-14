import org.hibernate.Hibernate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;

public class DeleteDoctorNoteFromPanel extends Box{
    protected final MyComboBox<DoctorNote> doctorNoteMyComboBox = new MyComboBox<>((v) ->
            v.getDisease() + " " + v.getDate());
    protected final MyComboBox<Patient> patientMyComboBox = new MyComboBox<>();
    protected List<DoctorNote> doctorNoteList;
    protected List<Patient> patientList;

    public DeleteDoctorNoteFromPanel() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        JLabel labelTitle = new JLabel("Удаление справки", SwingConstants.CENTER);
        JLabel labelPatient = new JLabel("Пациент", SwingConstants.CENTER);
        JLabel labelDoctorNote = new JLabel("Справка", SwingConstants.CENTER);
        JButton buttonDelete = new JButton("Удалить справку");
        labelTitle.setMaximumSize(new Dimension(350, 30));
        labelPatient.setMaximumSize(new Dimension(100, 30));
        labelDoctorNote.setMaximumSize(new Dimension(100, 30));
        doctorNoteMyComboBox.getComboBox().setMaximumSize(new Dimension(250, 30));
        patientMyComboBox.getComboBox().setMaximumSize(new Dimension(250, 30));
        patientMyComboBox.getComboBox().addActionListener(this::actionPerformedChooseCombo);
        buttonDelete.setMaximumSize(new Dimension(350, 30));
        buttonDelete.addActionListener(this::actionPerformedDeleteButton);

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(labelTitle)
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelPatient)
                        .addComponent(patientMyComboBox.getComboBox()))
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelDoctorNote)
                        .addComponent(doctorNoteMyComboBox.getComboBox()))
                .addGap(5)
                .addComponent(buttonDelete));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(labelTitle)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelPatient)
                        .addComponent(patientMyComboBox.getComboBox()))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelDoctorNote)
                        .addComponent(doctorNoteMyComboBox.getComboBox()))
                .addComponent(buttonDelete));

        add(createVerticalGlue());
        add(panel);
        add(createVerticalGlue());
    }

    protected void updateComboBox() {
        Hospital.session(session -> {
            patientList = session.createQuery("select a from Patient a", Patient.class).getResultList();
            for(Patient p:patientList){
                Hibernate.initialize(p.getDoctorNotes());
            }
            doctorNoteList = session.createQuery("select a from DoctorNote a", DoctorNote.class).getResultList();
        });
        patientMyComboBox.update(patientList);
        doctorNoteMyComboBox.update(doctorNoteList);
        actionPerformedChooseCombo(null);
    }

    public void actionPerformedChooseCombo(ActionEvent e) {
        if (patientMyComboBox.getSelectedItem() == null) return;
        doctorNoteMyComboBox.update(patientMyComboBox.getSelectedItem().getDoctorNotes());

    }

    public void actionPerformedDeleteButton(ActionEvent e) {
        Hospital.session(session -> session.delete(doctorNoteMyComboBox.getSelectedItem()));
        Hospital.logger.log(Level.INFO, "Removal a doctor note for patient " +
                doctorNoteMyComboBox.getSelectedItem().getPatient().toString() + ", doctor " +
                doctorNoteMyComboBox.getSelectedItem().getDoctor().toString());
        Hospital.showDialog("Справка удалена", JOptionPane.INFORMATION_MESSAGE);
    }
}
