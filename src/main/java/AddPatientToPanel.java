import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

public class AddPatientToPanel extends Box{
    protected final JTextField fieldFirstName = new JTextField(),
            fieldLastName = new JTextField();
    public JDatePickerImpl datePicker;

    public AddPatientToPanel() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        JLabel labelTitle = new JLabel("Добавить нового пациента", SwingConstants.CENTER);
        JLabel labelFirstName = new JLabel("Имя", SwingConstants.CENTER);
        JLabel labelLastName = new JLabel("Фамилия", SwingConstants.CENTER);
        JLabel labelDateOfBirth = new JLabel("Дата рождения", SwingConstants.CENTER);
        JButton buttonAdd = new JButton("Добавить");

        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.getJFormattedTextField().setBackground(Color.WHITE);
        model.setDate(1990, 6, 1);
        model.setSelected(true);

        labelTitle.setMaximumSize(new Dimension(350, 30));
        labelFirstName.setMaximumSize(new Dimension(100, 10));
        fieldFirstName.setMaximumSize(new Dimension(250, 10));
        labelLastName.setMaximumSize(new Dimension(100, 10));
        fieldLastName.setMaximumSize(new Dimension(250, 10));
        labelDateOfBirth.setMaximumSize(new Dimension(100, 10));
        datePicker.setMaximumSize(new Dimension(250, 10));
        buttonAdd.setMaximumSize(new Dimension(350, 30));

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(labelTitle)
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
                .addComponent(buttonAdd));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(labelTitle)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelFirstName)
                        .addComponent(fieldFirstName))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelLastName)
                        .addComponent(fieldLastName))
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(labelDateOfBirth)
                        .addComponent(datePicker))
                .addComponent(buttonAdd));

        add(createVerticalGlue());
        add(panel);
        add(createVerticalGlue());

        buttonAdd.addActionListener(this::actionPerformedAddButton);
    }

    private boolean checkFields(String date) {
        return !fieldFirstName.getText().isEmpty() &&
                !fieldLastName.getText().isEmpty() &&
                date.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$");
    }

    public void actionPerformedAddButton(ActionEvent e) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        String dateOfBirth =  formatForDateNow.format((Date) datePicker.getModel().getValue());

        if(!checkFields(dateOfBirth)) {
            Hospital.logger.log(Level.WARNING, "Adding a patient is impossible, because the fields are filled incorrectly");
            Hospital.showDialog("Поля заполнены неверно", JOptionPane.ERROR_MESSAGE);
        }
        else {
            Hospital.session((s) -> s.save(new Patient(fieldFirstName.getText(), fieldLastName.getText(), dateOfBirth)));
            Hospital.logger.log(Level.INFO, "Adding a patient " + fieldFirstName.getText() + " " + fieldLastName.getText());
            Hospital.showDialog("Пациент добавлен", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
