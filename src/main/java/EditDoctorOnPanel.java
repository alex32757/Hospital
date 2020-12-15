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

public class EditDoctorOnPanel extends Box {
    protected final JTextField fieldFirstName = new JTextField(),
                               fieldSpecialization = new JTextField(),
                               fieldLastName = new JTextField();
    public JDatePickerImpl datePicker;
    public UtilDateModel model;
    protected final MyComboBox<Doctor> doctorMyComboBox = new MyComboBox<>();
    protected List<Doctor> doctorList;

    public EditDoctorOnPanel() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        JLabel labelTitle = new JLabel("Изменение информации о враче", SwingConstants.CENTER);
        JLabel labelFirstName = new JLabel("Имя", SwingConstants.CENTER);
        JLabel labelLastName = new JLabel("Фамилия", SwingConstants.CENTER);
        JLabel labelDateOfBirth = new JLabel("Дата рождения", SwingConstants.CENTER);
        JLabel labelSpecialization = new JLabel("Специализация", SwingConstants.CENTER);
        JButton buttonEdit = new JButton("Обновить информацию");
        JButton buttonDelete = new JButton("Удалить врача");


        model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.getJFormattedTextField().setBackground(Color.WHITE);

        labelTitle.setMaximumSize(new Dimension(350, 30));
        labelFirstName.setMaximumSize(new Dimension(100, 30));
        fieldFirstName.setMaximumSize(new Dimension(250, 30));
        labelLastName.setMaximumSize(new Dimension(100, 30));
        fieldLastName.setMaximumSize(new Dimension(250, 30));
        labelSpecialization.setMaximumSize(new Dimension(100, 30));
        fieldSpecialization.setMaximumSize(new Dimension(250, 30));
        labelDateOfBirth.setMaximumSize(new Dimension(100, 30));
        datePicker.setMaximumSize(new Dimension(250, 10));
        doctorMyComboBox.getComboBox().setMaximumSize(new Dimension(350, 30));
        doctorMyComboBox.getComboBox().addActionListener(this::actionPerformedUpdate);
        buttonEdit.setMaximumSize(new Dimension(175, 30));
        buttonDelete.setMaximumSize(new Dimension(175, 30));

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(labelTitle)
                .addGap(5)
                .addComponent(doctorMyComboBox.getComboBox())
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
                        .addComponent(labelSpecialization)
                        .addComponent(fieldSpecialization))
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(buttonEdit)
                        .addComponent(buttonDelete)));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(labelTitle)
                .addComponent(doctorMyComboBox.getComboBox())
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
                        .addComponent(labelSpecialization)
                        .addComponent(fieldSpecialization))
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
        Hospital.session(session -> doctorList = session.createQuery("select a from Doctor a", Doctor.class).getResultList());
        fieldFirstName.setText("");
        fieldLastName.setText("");
        fieldSpecialization.setText(null);
        doctorMyComboBox.update(doctorList);
        actionPerformedUpdate(null);
    }

    private boolean checkFields(String date) {
        return !fieldFirstName.getText().isEmpty() &&
                !fieldLastName.getText().isEmpty() &&
                !fieldSpecialization.getText().isEmpty() &&
                date.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$");
    }

    public void actionPerformedUpdate(ActionEvent e) {
        Doctor doctor = doctorMyComboBox.getSelectedItem();
        if(doctorMyComboBox.getSelectedItem() == null) return;
        fieldFirstName.setText(doctor.getFirstName());
        fieldLastName.setText(doctor.getLastName());
        fieldSpecialization.setText(doctor.getSpecialization());
        String[] subStr;
        String date = doctor.getDateOfBirth();
        subStr = date.split("\\.");
        model.setDate(Integer.parseInt(subStr[2]), Integer.parseInt(subStr[1]) - 1, Integer.parseInt(subStr[0]));
        model.setSelected(true);
    }

    public void actionPerformedAddButton(ActionEvent e) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        String dateOfBirth =  formatForDateNow.format((Date) datePicker.getModel().getValue());

        if(!checkFields(dateOfBirth)) {
            Hospital.logger.log(Level.WARNING, "Editing a doctor is impossible, because the fields are filled incorrectly");
            Hospital.showDialog("Поля заполнены неверно", JOptionPane.ERROR_MESSAGE);
        }
        else {
            Doctor doctor = doctorMyComboBox.getSelectedItem();
            doctor.setFirstName(fieldFirstName.getText());
            doctor.setLastName(fieldLastName.getText());
            doctor.setDateOfBirth(dateOfBirth);
            doctor.setSpecialization(fieldSpecialization.getText());
            Hospital.session((s) ->
                    s.update(doctor)
            );
            Hospital.logger.log(Level.INFO, "Editing a doctor " + fieldFirstName.getText() + " " + fieldLastName.getText());
            Hospital.showDialog("Информация о докторе обновлена", JOptionPane.INFORMATION_MESSAGE);
        }
        updateComboBox();
    }
    public void actionPerformedAddButtonDelete(ActionEvent e) {
        if (doctorMyComboBox.getSelectedItem() != null) {
            if(Hospital.confirmDialog("Вы уверены, что хотите удалить " + doctorMyComboBox.getSelectedItem().toString() +"?", JOptionPane.OK_CANCEL_OPTION) == 0) {
                Hospital.session(session -> session.delete(doctorMyComboBox.getSelectedItem()));
                Hospital.logger.log(Level.INFO, "Deleting a doctor " + doctorMyComboBox.getSelectedItem().toString());
                Hospital.showDialog("Доктор удалён", JOptionPane.INFORMATION_MESSAGE);
                updateComboBox();
            }
        }
        else {
            Hospital.logger.log(Level.WARNING, "Deleting a doctor is impossible, because the doctor is not selected");
            Hospital.showDialog("Доктор не выбран", JOptionPane.ERROR_MESSAGE);
        }
    }
}

