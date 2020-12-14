import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class AddNoteToPanel extends Box{
    protected final JTextField fieldDisease = new JTextField();
    public JDatePickerImpl datePicker;
    protected final MyComboBox<Doctor> doctorMyComboBox = new MyComboBox<>();
    protected final MyComboBox<Patient> patientMyComboBox = new MyComboBox<>();
    protected List<Doctor> doctorList;
    protected List<Patient> patientList;

    public AddNoteToPanel() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        JLabel labelTitle = new JLabel("Создание справки", SwingConstants.CENTER);
        JLabel labelPatient = new JLabel("Пациент", SwingConstants.CENTER);
        JLabel labelDoctor = new JLabel("Врач", SwingConstants.CENTER);
        JLabel labelDate = new JLabel("Дата", SwingConstants.CENTER);
        JLabel labelDisease = new JLabel("Диагноз", SwingConstants.CENTER);
        JButton buttonCreate = new JButton("Создать справку");

        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.getJFormattedTextField().setBackground(Color.WHITE);

        labelTitle.setMaximumSize(new Dimension(350, 30));
        labelPatient.setMaximumSize(new Dimension(100, 30));
        patientMyComboBox.getComboBox().setMaximumSize(new Dimension(250, 30));
        labelDoctor.setMaximumSize(new Dimension(100, 30));
        doctorMyComboBox.getComboBox().setMaximumSize(new Dimension(250, 30));
        labelDate.setMaximumSize(new Dimension(100, 30));
        datePicker.setMaximumSize(new Dimension(250, 10));
        labelDisease.setMaximumSize(new Dimension(100, 30));
        fieldDisease.setMaximumSize(new Dimension(250, 30));
        buttonCreate.setMaximumSize(new Dimension(350, 30));

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(labelTitle)
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelPatient)
                        .addComponent(patientMyComboBox.getComboBox()))
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelDoctor)
                        .addComponent(doctorMyComboBox.getComboBox()))
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelDisease)
                        .addComponent(fieldDisease))
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelDate)
                        .addComponent(datePicker))

                .addGap(5)
                .addComponent(buttonCreate));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(labelTitle)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelPatient)
                        .addComponent(patientMyComboBox.getComboBox()))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelDoctor)
                        .addComponent(doctorMyComboBox.getComboBox()))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelDisease)
                        .addComponent(fieldDisease))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelDate)
                        .addComponent(datePicker))
                .addComponent(buttonCreate));

        add(createVerticalGlue());
        add(panel);
        add(createVerticalGlue());

        buttonCreate.addActionListener(this::actionPerformedCreateButton);
    }

    protected void updateComboBox() {
        Hospital.session(session -> {
            doctorList = session.createQuery("select a from Doctor a",
                    Doctor.class).getResultList();
            patientList = session.createQuery("select a from Patient a",
                    Patient.class).getResultList();
        });
        fieldDisease.setText(null);
        doctorMyComboBox.update(doctorList);
        patientMyComboBox.update(patientList);
    }

    private boolean checkFields(String date) {
        boolean b = !fieldDisease.getText().isEmpty() &&
                date.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$");
        return b;
    }

    public void actionPerformedCreateButton(ActionEvent e) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        String dateOfBirth = formatForDateNow.format((Date) datePicker.getModel().getValue());

        if(!checkFields(dateOfBirth)) {
            Hospital.logger.log(Level.WARNING, "Adding a doctor note is impossible, because the fields are filled incorrectly");
            Hospital.showDialog("Поля заполнены неверно", JOptionPane.ERROR_MESSAGE);
        }
        else {
            DoctorNote doctorNote = new DoctorNote(patientMyComboBox.getSelectedItem(), doctorMyComboBox.getSelectedItem(),
                    dateOfBirth, fieldDisease.getText());
            Hospital.session((s) -> s.save(doctorNote));
            Hospital.logger.log(Level.INFO, "Adding a doctor note for patient " + patientMyComboBox.getSelectedItem().toString() +
                    ", doctor " + doctorMyComboBox.getSelectedItem().toString());
            Hospital.showDialog("Справка добавлена", JOptionPane.INFORMATION_MESSAGE);
            fieldDisease.setText(null);
        }
    }

}
