import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TableDoctor extends Box {
    private final JTable table = new JTable();
    private final String[] columnName = {"id", "first_name", "last_name", "specialization"};
    private final ArrayList<String[]> tableList = new ArrayList<>();
    private List<Doctor> doctorList;

    public TableDoctor() {
        super(BoxLayout.Y_AXIS);

        JPanel panel = new JPanel();
        JScrollPane tableScroll = new JScrollPane(table);
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        table.setShowGrid(true);

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(tableScroll));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(tableScroll));

        add(panel);
    }

    public void update() {
        tableList.clear();
        Hospital.session(session -> {
            doctorList = session.createQuery("select a from Doctor a", Doctor.class).getResultList();
        });

        for(Doctor d:doctorList) {
            String[] str = {String.valueOf(d.getId()), d.getFirstName(), d.getLastName(), d.getSpecialization()};
            tableList.add(str);
        }

        TableModel tableModel = new DefaultTableModel(
                tableList.toArray(new String[0][0]),
                columnName);
        table.setModel(tableModel);
        Hospital.logger.log(Level.INFO, "Showing a doctor table");
    }
}
