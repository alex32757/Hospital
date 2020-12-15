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

public class EditPatientOnPanel extends Box {
    protected final JTextField fieldFirstName = new JTextField(),
                               fieldLastName = new JTextField();
    public JDatePickerImpl datePicker;
    public UtilDateModel model;
    protected final MyComboBox<Patient> patientMyComboBox = new MyComboBox<>();
    protected List<Patient> patientList;

    public EditPatientOnPanel() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        JLabel labelTitle = new JLabel("Изменение информации о пациенте", SwingConstants.CENTER);
        JLabel labelFirstName = new JLabel("Имя", SwingConstants.CENTER);
        JLabel labelLastName = new JLabel("Фамилия", SwingConstants.CENTER);
        JLabel labelDateOfBirth = new JLabel("Дата рождения", SwingConstants.CENTER);
        JButton buttonEdit = new JButton("Обновить информацию");
        JButton buttonDelete = new JButton("Удалить пациента");

        model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.getJFormattedTextField().setBackground(Color.WHITE);

        labelTitle.setMaximumSize(new Dimension(350, 30));
        labelTitle.setMaximumSize(new Dimension(350, 30));
        labelFirstName.setMaximumSize(new Dimension(100, 10));
        fieldFirstName.setMaximumSize(new Dimension(250, 10));
        labelLastName.setMaximumSize(new Dimension(100, 10));
        fieldLastName.setMaximumSize(new Dimension(250, 10));
        labelDateOfBirth.setMaximumSize(new Dimension(100, 10));
        datePicker.setMaximumSize(new Dimension(250, 10));
        patientMyComboBox.getComboBox().setMaximumSize(new Dimension(350, 30));
        patientMyComboBox.getComboBox().addActionListener(this::actionPerformedUpdate);
        buttonEdit.setMaximumSize(new Dimension(175, 30));
        buttonDelete.setMaximumSize(new Dimension(175, 30));

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(labelTitle)
                .addGap(5)
                .addComponent(patientMyComboBox.getComboBox())
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelFirstName)
                        .addComponent(fieldFirstName))
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelLastName)
                        .addComponent(fieldLastName))
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(labelDateOfBirth)
                        .addComponent(datePicker))
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(buttonEdit)
                        .addComponent(buttonDelete)));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(labelTitle)
                .addComponent(patientMyComboBox.getComboBox())
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelFirstName)
                        .addComponent(fieldFirstName))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelLastName)
                        .addComponent(fieldLastName))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelDateOfBirth)
                        .addComponent(datePicker))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(buttonEdit)
                        .addComponent(buttonDelete)));

        add(createVerticalGlue());
        add(panel);
        add(createVerticalGlue());

        buttonEdit.addActionListener(this::actionPerformedAddButton);
        buttonDelete.addActionListener((this::actionPerformedAddButtonDelete));
    }

    protected void updateComboBox() {
        Hospital.session(session -> patientList = session.createQuery("select a from Patient a", Patient.class).getResultList());
        fieldFirstName.setText("");
        fieldLastName.setText("");
        patientMyComboBox.update(patientList);
        actionPerformedUpdate(null);
    }

    private boolean checkFields(String date) {
        return !fieldFirstName.getText().isEmpty() &&
                !fieldLastName.getText().isEmpty() &&
                date.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$");
    }
    public void actionPerformedUpdate(ActionEvent e) {
        Patient patient = patientMyComboBox.getSelectedItem();
        if(patientMyComboBox.getSelectedItem() == null) return;
        fieldFirstName.setText(patient.getFirstName());
        fieldLastName.setText(patient.getLastName());
        String[] subStr;
        String date = patient.getDateOfBirth();
        subStr = date.split("\\.");
        model.setDate(Integer.parseInt(subStr[2]), Integer.parseInt(subStr[1]) - 1, Integer.parseInt(subStr[0]));
        model.setSelected(true);
    }

    public void actionPerformedAddButton(ActionEvent e) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        String dateOfBirth =  formatForDateNow.format((Date) datePicker.getModel().getValue());

        if(!checkFields(dateOfBirth)) {
            Hospital.logger.log(Level.WARNING, "Editing a patient is impossible, because the fields are filled incorrectly");
            Hospital.showDialog("Поля заполнены неверно", JOptionPane.ERROR_MESSAGE);
        }
        else {
            Patient patient = patientMyComboBox.getSelectedItem();
            patient.setFirstName(fieldFirstName.getText());
            patient.setLastName(fieldLastName.getText());
            patient.setDateOfBirth(dateOfBirth);
            Hospital.session((s) ->
                s.update(patient)
            );
            Hospital.logger.log(Level.INFO, "Editing a patient " + fieldFirstName.getText() + " " + fieldLastName.getText());
            Hospital.showDialog("Информация о пациенте обновлена", JOptionPane.INFORMATION_MESSAGE);
        }
        updateComboBox();
    }

    public void actionPerformedAddButtonDelete(ActionEvent e) {
        if (patientMyComboBox.getSelectedItem() != null) {
            if(Hospital.confirmDialog("Вы уверены, что хотите удалить " + patientMyComboBox.getSelectedItem().toString() + "?", JOptionPane.OK_CANCEL_OPTION) == 0) {
                Hospital.session(session -> session.delete(patientMyComboBox.getSelectedItem()));
                Hospital.logger.log(Level.INFO, "Deleting a patient " + patientMyComboBox.getSelectedItem().toString());
                Hospital.showDialog("Пациент удалён", JOptionPane.INFORMATION_MESSAGE);
                updateComboBox();
            }
        }
        else {
            Hospital.logger.log(Level.WARNING, "Deleting a patient is impossible, because the patient is not selected");
            Hospital.showDialog("Пациент не выбран", JOptionPane.ERROR_MESSAGE);
        }
    }
}

