import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TableSchedule extends Box {
    private final JTable table = new JTable();
    private final String[] columnName = {"day_of_week", "start_time", "end_time", "cab"};
    private final ArrayList<String[]> tableList = new ArrayList<>();
    private List<Doctor> doctorList;
    protected final MyComboBox<Doctor> doctorMyComboBox = new MyComboBox<>();
    private List<WorkTime> workTimeList;

    public TableSchedule() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        JScrollPane tableScroll = new JScrollPane(table);
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);
        table.setShowGrid(true);

        doctorMyComboBox.getComboBox().setMaximumSize(new Dimension(300, 30));
        doctorMyComboBox.getComboBox().addActionListener(this::actionPerformedChooseCombo);

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGap(5)
                .addComponent(doctorMyComboBox.getComboBox())
                .addGap(5)
                .addComponent(tableScroll));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(doctorMyComboBox.getComboBox())
                .addComponent(tableScroll));

        add(panel);
    }

    protected void updateComboBox() {
        Hospital.session(session -> doctorList = session.createQuery("select a from Doctor a", Doctor.class).getResultList());
        doctorMyComboBox.update(doctorList);
    }

    public void actionPerformedChooseCombo(ActionEvent e) {
        tableList.clear();
        Doctor doctor = doctorMyComboBox.getSelectedItem();
        if(doctor != null) {
            Hospital.session(session -> workTimeList = session.createQuery("select w from WorkTime w where w.doctor.id = :id",
                    WorkTime.class).setParameter("id", doctor.getId()).getResultList());
            for (WorkTime wt : workTimeList) {
                String[] str = {Hospital.returnDayOfWeek(wt.getDayOfWeek()), wt.getStartTime(), wt.getEndTime(), String.valueOf(wt.getCabNumber())};
                tableList.add(str);
            }

            TableModel tableModel = new DefaultTableModel(
                    tableList.toArray(new String[0][0]),
                    columnName);
            table.setModel(tableModel);
            Hospital.logger.log(Level.INFO, "Showing a schedule for doctor " + doctorMyComboBox.getSelectedItem().toString());
        }
    }

}
