import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class TableDiseaseStat extends Box {
    private final JTable table = new JTable();
    private final String[] columnName = {"Disease", "Count"};
    private final ArrayList<String[]> tableList = new ArrayList<>();
    private List<DoctorNote> doctorNoteList;
    private ArrayList<DiseaseStat> diseaseStatList = new ArrayList<DiseaseStat>();

    public TableDiseaseStat() {
        super(BoxLayout.Y_AXIS);

        JPanel panel = new JPanel();
        JButton reportButton = new JButton("Отчёт");
        JScrollPane tableScroll = new JScrollPane(table);
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);
        table.setShowGrid(true);

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(tableScroll)
                .addComponent(reportButton)
                .addGap(5));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(tableScroll)
                .addComponent(reportButton));

        reportButton.setMaximumSize(new Dimension(100, 30));
        reportButton.addActionListener(this::actionPerformedAddButton);
        add(panel);
    }

    public void actionPerformedAddButton(ActionEvent e) {
        try {
            JasperReport report = JasperCompileManager.compileReport(new FileInputStream(
                    "C:\\Users\\alex3\\JaspersoftWorkspace\\MyReports\\DiseaseStat.jrxml"));
            JRBeanCollectionDataSource collectionDataSource = new JRBeanCollectionDataSource(diseaseStatList);
            JasperPrint print = JasperFillManager.fillReport(report, null, collectionDataSource);

            Thread threadPdf = new Thread(() -> {
                try {
                    JasperExportManager.exportReportToPdfFile(print, "D:\\Hospital_report.pdf");
                } catch (JRException jrException) {
                    Hospital.logger.log(Level.WARNING, "Error in thread creation (threadPdf)");
                    jrException.printStackTrace();
                }
            });
            Thread threadHtml = new Thread(() -> {
                try {
                    JasperExportManager.exportReportToHtmlFile(print, "D:\\Hospital_report.html");
                } catch (JRException jrException) {
                    Hospital.logger.log(Level.WARNING, "Error in thread creation (threadHtml)");
                    jrException.printStackTrace();
                }
            });
            threadPdf.start();
            threadHtml.start();
            threadPdf.join();
            threadHtml.join();
            Hospital.logger.log(Level.INFO, "Report creating complete");
            Hospital.showDialog("Отчёт по статистике заболеваемости сгенерирован", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception ex) {
            Hospital.logger.log(Level.WARNING, "Error in creating report");
            ex.printStackTrace();
            Hospital.showDialog("Ошибка в генерации отчёта по статистике заболеваемости", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void update() {
        Hospital.session(session -> {
            doctorNoteList = session.createQuery("select a from DoctorNote a", DoctorNote.class).getResultList();
        });
        tableList.clear();
        HashMap<String, Integer> map = new HashMap<>();
        for(DoctorNote d:doctorNoteList) {
            Integer i = map.get(d.getDisease().toLowerCase());
            if(i == null) i = 0;
            i++;
            map.put(d.getDisease().toLowerCase(), i);
        }
        diseaseStatList.clear();
        map.forEach((key, tab) -> {
            diseaseStatList.add(new DiseaseStat(key, tab));
            String[] str = {key, String.valueOf(tab)};
            tableList.add(str);
        });

        TableModel tableModel = new DefaultTableModel(
            tableList.toArray(new String[0][0]),
            columnName);
        table.setModel(tableModel);
        Hospital.logger.log(Level.INFO, "Showing a disease statistics");
    }

    public static class DiseaseStat {
        private String disease;
        private int count;

        public DiseaseStat(String disease, int count) {
            this.disease = disease;
            this.count = count;
        }

        public String getDisease() {
            return disease;
        }

        public void setDisease(String disease) {
            this.disease = disease;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}



