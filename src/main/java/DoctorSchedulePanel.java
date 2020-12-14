import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DoctorSchedulePanel extends Box {
    public ArrayList<WorkTime> workTimes = new ArrayList<>();
    private int selectedDay = 0;
    protected final JTextField fieldCab1 = new JTextField(),
                    fieldStartTime1 = new JTextField(),
                    fieldEndTime1 = new JTextField();
    protected final MyComboBox<Doctor> doctorMyComboBox = new MyComboBox<>();
    protected List<Doctor> doctorList;

    public DoctorSchedulePanel() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        JLabel labelTitle = new JLabel("Расписание", SwingConstants.CENTER);
        JLabel labelCab = new JLabel("Номер кабинета", SwingConstants.CENTER);
        JLabel labelStartTime = new JLabel("Начало приёма", SwingConstants.CENTER);
        JLabel labelEndTime = new JLabel("Окончание приёма", SwingConstants.CENTER);
        JButton buttonAdd = new JButton("Добавить в расписание");
        JButton buttonSave = new JButton("Сохранить");


        String[] weekDaysList = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};
        final JList<String> list = new JList<>(weekDaysList);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setPrototypeCellValue("Увеличенный");
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedDay = list.locationToIndex(e.getPoint()) + 1;
               // System.out.println(selectedDay);
            }
        });

        labelTitle.setMaximumSize(new Dimension(500, 30));

        labelCab.setMaximumSize(new Dimension(150, 30));
        labelStartTime.setMaximumSize(new Dimension(150, 30));
        labelEndTime.setMaximumSize(new Dimension(150, 30));
        buttonAdd.setMaximumSize(new Dimension(250, 30));
        buttonSave.setMaximumSize(new Dimension(250, 30));
        fieldCab1.setMaximumSize(new Dimension(250, 30));
        fieldStartTime1.setMaximumSize(new Dimension(250, 30));
        fieldEndTime1.setMaximumSize(new Dimension(250, 30));
        doctorMyComboBox.getComboBox().setMaximumSize(new Dimension(500, 30));
        list.setSize(new Dimension(100,300));

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(labelTitle)
                .addGap(5)
                .addComponent(doctorMyComboBox.getComboBox())
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(list)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup()
                                .addComponent(labelCab)
                                .addComponent(fieldCab1))
                            .addGroup(groupLayout.createParallelGroup()
                                .addComponent(labelStartTime)
                                .addComponent(fieldStartTime1))
                            .addGroup(groupLayout.createParallelGroup()
                                .addComponent(labelEndTime)
                                .addComponent(fieldEndTime1))))
                .addGap(5)
                .addComponent(buttonAdd)
                .addGap(10)
                .addComponent(buttonSave));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(labelTitle)
                .addComponent(doctorMyComboBox.getComboBox())
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(list)
                        .addGroup(groupLayout.createParallelGroup()
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(labelCab)
                                        .addComponent(fieldCab1))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(labelStartTime)
                                        .addComponent(fieldStartTime1))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(labelEndTime)
                                        .addComponent(fieldEndTime1))))
                .addComponent(buttonAdd)
                .addComponent(buttonSave));

        add(createVerticalGlue());
        add(panel);
        add(createVerticalGlue());

        buttonAdd.addActionListener(this::actionPerformedAddButton);
        buttonSave.addActionListener(this::actionPerformedAddButtonSave);
        doctorMyComboBox.getComboBox().addActionListener(v -> workTimes.clear());
    }

    protected void updateComboBox() {
        Hospital.session(session -> doctorList = session.createQuery("select a from Doctor a", Doctor.class).getResultList());
        fieldCab1.setText("");
        fieldStartTime1.setText("");
        fieldEndTime1.setText("");
        doctorMyComboBox.update(doctorList);
    }

    private boolean checkFields() {
        return !fieldCab1.getText().isEmpty() &&
                fieldStartTime1.getText().matches("^\\d{2}:\\d{2}$") &&
                fieldEndTime1.getText().matches("^\\d{2}:\\d{2}$");
    }

    public void actionPerformedAddButton(ActionEvent e) {
        if(!checkFields() || selectedDay == 0) {
            Hospital.logger.log(Level.WARNING, "Adding a doctor schedule is impossible, because the fields are filled incorrectly");
            Hospital.showDialog("Поля заполнены неверно", JOptionPane.ERROR_MESSAGE);
        }
        else {
            Doctor doctor = doctorMyComboBox.getSelectedItem();
            WorkTime workTime = new WorkTime(doctor, selectedDay, fieldStartTime1.getText(), fieldEndTime1.getText(),
                    Integer.parseInt(fieldCab1.getText()));
            workTimes.add(workTime);
            Hospital.logger.log(Level.INFO, "Adding a schedule for doctor " + doctorMyComboBox.getSelectedItem().toString() +
                    " on " + selectedDay + " day");
            Hospital.showDialog("В список расписания добавлено расписание на " + Hospital.returnDayOfWeek(selectedDay),
                    JOptionPane.INFORMATION_MESSAGE);
            fieldCab1.setText(null);
            fieldStartTime1.setText(null);
            fieldEndTime1.setText(null);
        }
    }

    public void actionPerformedAddButtonSave(ActionEvent e) {
        if(workTimes.isEmpty()) {
            Hospital.logger.log(Level.WARNING, "Adding a doctor schedule is impossible, because the schedule is empty");
            Hospital.showDialog("Список расписания пуст", JOptionPane.ERROR_MESSAGE);
        }
        else {
            Doctor doctor =  doctorMyComboBox.getSelectedItem();
            doctor.setSchedule(workTimes);
            Session session = Hospital.sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("delete from worktime where doctor_id = :id").setParameter("id", doctor.getId()).executeUpdate();
            workTimes.forEach(session::save);
            transaction.commit();
            session.close();
            Hospital.logger.log(Level.INFO, "Adding a schedule for doctor " + doctorMyComboBox.getSelectedItem().toString());
            Hospital.showDialog("Расписание обновлено", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
