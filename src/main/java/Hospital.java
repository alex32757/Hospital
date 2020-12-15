import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hospital extends Box{
    public static SessionFactory sessionFactory = new Configuration()
            .configure()
            .buildSessionFactory();
    public static JFrame frame = new JFrame("Поликлиника");
    public static final Logger logger = Logger.getLogger(Hospital.class.getName());

    public static void session(SessionRequest request) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        request.run(session);
        session.getTransaction().commit();
        session.close();
    }

    public interface SessionRequest {
        void run(Session session);
    }

    public static void showDialog(String text, int type) {
        JOptionPane.showMessageDialog(frame, text, "Сообщение", type);
    }

    public static String returnDayOfWeek(int day) {
        return switch (day) {
            case 1 -> "Понедельник";
            case 2 -> "Вторник";
            case 3 -> "Среда";
            case 4 -> "Четверг";
            case 5 -> "Пятница";
            case 6 -> "Суббота";
            default -> throw new IllegalStateException("Unexpected value: " + day);
        };
    }

    public Hospital() {
        super(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);

        JLabel labelTitle = new JLabel("Работа с БД \"Поликлиника\", выберите пункт меню", SwingConstants.CENTER);
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(labelTitle));

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(labelTitle));

        add(createVerticalGlue());
        add(panel);
        add(createVerticalGlue());
        Hospital.logger.log(Level.INFO, "Starting a program");
    }

    public static void main(String[] args) {
        FileHandler fh;

        try {
            fh = new FileHandler("D:/hospital.log");
            logger.addHandler(fh);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        Hospital hospital = new Hospital();
        AddPatientToPanel addPatientToPanel = new AddPatientToPanel();
        AddDocToPanel addDocToPanel = new AddDocToPanel();
        DoctorSchedulePanel doctorSchedulePanel = new DoctorSchedulePanel();
        EditPatientOnPanel editPatientOnPanel = new EditPatientOnPanel();
        EditDoctorOnPanel editDoctorOnPanel = new EditDoctorOnPanel();
        AddNoteToPanel addNoteToPanel = new AddNoteToPanel();
        DeleteDoctorNoteFromPanel deleteDoctorNoteFromPanel = new DeleteDoctorNoteFromPanel();
        TableDoctor tableDoctor = new TableDoctor();
        TableDoctorNote tableDoctorNote = new TableDoctorNote();
        TableDiseaseStat tableDiseaseStat = new TableDiseaseStat();
        TableSchedule tableSchedule = new TableSchedule();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setLocationRelativeTo(null);
        frame.setContentPane(hospital);
        frame.revalidate();
        frame.repaint();

        JMenuBar menuBar = new JMenuBar();
        JMenu menuPatient = new JMenu("Пациенты");
        JMenuItem addPatient = new JMenuItem("Добавить"),
                changePatient = new JMenuItem("Изменить/Удалить информацию"),
                patientNode = new JMenuItem("Показать справки");
        menuPatient.add(addPatient);
        menuPatient.add(changePatient);
        menuPatient.add(patientNode);
        menuBar.add(menuPatient);

        JMenu menuDoctor = new JMenu("Врачи");
        JMenuItem addDoctor = new JMenuItem("Добавить"),
                changeDoctor = new JMenuItem("Изменить/Удалить информацию"),
                scheduleDoctor = new JMenuItem("Добавить/Редактировать расписание"),
                doctorInfo = new JMenuItem("Информация");
        menuDoctor.add(addDoctor);
        menuDoctor.add(changeDoctor);
        menuDoctor.add(scheduleDoctor);
        menuDoctor.add(doctorInfo);
        menuBar.add(menuDoctor);

        JMenu menuNote = new JMenu("Справки");
        JMenuItem addNote = new JMenuItem("Создать справку");
        JMenuItem deleteNote = new JMenuItem("Удалить справку");
        menuNote.add(addNote);
        menuNote.add(deleteNote);
        menuBar.add(menuNote);

        JMenu menuSchedule = new JMenu("График работы");
        JMenuItem viewSchedule = new JMenuItem("График работы");
        menuSchedule.add(viewSchedule);
        menuBar.add(menuSchedule);

        JMenu menuStat = new JMenu("Статистика заболеваемости");
        JMenuItem viewStat = new JMenuItem("Статистика заболеваемости");
        menuStat.add(viewStat);
        menuBar.add(menuStat);
        frame.setJMenuBar(menuBar);

        addPatient.addActionListener((e) -> {
            frame.setContentPane(addPatientToPanel);
            frame.revalidate();
            frame.repaint();
        });
        addDoctor.addActionListener((e) -> {
            frame.setContentPane(addDocToPanel);
            frame.revalidate();
            frame.repaint();
        });
        scheduleDoctor.addActionListener((e) -> {
            doctorSchedulePanel.updateComboBox();
            frame.setContentPane(doctorSchedulePanel);
            frame.revalidate();
            frame.repaint();
        });
        changePatient.addActionListener((e -> {
            editPatientOnPanel.updateComboBox();
            frame.setContentPane(editPatientOnPanel);
            frame.revalidate();
            frame.repaint();
        }));
        changeDoctor.addActionListener((e ->{
            editDoctorOnPanel.updateComboBox();
            frame.setContentPane(editDoctorOnPanel);
            frame.revalidate();
            frame.repaint();
        }));
        addNote.addActionListener((e ->{
            addNoteToPanel.updateComboBox();
            frame.setContentPane(addNoteToPanel);
            frame.revalidate();
            frame.repaint();
        }));
        deleteNote.addActionListener((e -> {
            deleteDoctorNoteFromPanel.updateComboBox();
            frame.setContentPane(deleteDoctorNoteFromPanel);
            frame.revalidate();
            frame.repaint();
        }));
        doctorInfo.addActionListener((e -> {
            tableDoctor.update();
            frame.setContentPane(tableDoctor);
            frame.revalidate();
            frame.repaint();
        }));
        patientNode.addActionListener(e -> {
            tableDoctorNote.updateComboBox();
            frame.setContentPane(tableDoctorNote);
            frame.revalidate();
            frame.repaint();
        });
        viewStat.addActionListener(e -> {
            tableDiseaseStat.update();
            frame.setContentPane(tableDiseaseStat);
            frame.revalidate();
            frame.repaint();
        });
        viewSchedule.addActionListener(e -> {
            tableSchedule.updateComboBox();
            frame.setContentPane(tableSchedule);
            frame.revalidate();
            frame.repaint();
        });

        frame.setVisible(true);

    }

}

//