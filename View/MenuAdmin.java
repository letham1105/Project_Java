package View;

import Model.User;

import javax.swing.*;
import java.awt.*;

public class MenuAdmin extends JFrame {
    private final User loginUser;
    private JPanel panelView;
    private JButton createExamButton;
    private JButton createQuestionButton;
    private JButton examinationRoomButton;
    private JButton userButton;
    private JButton exitButton;

    public MenuAdmin(User user) {
        this.loginUser = user;
        addActionEvent();
        this.setTitle("Menu Admin");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    public static void main(String[] args) {
        User admin = new User("admin", "admin", "admin", true);
        EventQueue.invokeLater(() -> new MenuAdmin(admin));
    }

    private void addActionEvent() {
        userButton.addActionListener(event ->{
            this.dispose();
            new UserManagement(loginUser);
        });
        createExamButton.addActionListener(event ->{
            this.dispose();
            new ExamManagement(loginUser);
        });
        createQuestionButton.addActionListener(event ->{
            this.dispose();
            new QuestionManagement(loginUser);
        });
        examinationRoomButton.addActionListener( event -> {
            this.dispose();
            new ExaminationRoomManagement(loginUser);
        });
        exitButton.addActionListener(event ->{
           this.dispose();
           new Login();

        });
    }
    private void createUIComponent(){

    }


}
