package View;

import Model.User;

import javax.swing.*;
import java.awt.*;

public class MenuExaminee extends JFrame {
    private final User loginUser;
    private JButton enterRoomButton;
    private JButton logOutButton;
    private JButton scoreButton;
    private JPanel panelView;

    public MenuExaminee(User user)  {
        this.loginUser = user;
        addActionEvent();
        this.setTitle("Menu Examinee");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addActionEvent() {
        enterRoomButton.addActionListener(event ->{
            this.dispose();
            new EnterExamRoom(loginUser);
        });
        scoreButton.addActionListener(event ->{
            this.dispose();
            new ScoreOfExaminee(loginUser);
        });
        logOutButton.addActionListener(event ->{
            this.dispose();
            new Login();
        });
    }
private void createUIComponents(){

}
    public static void main(String[] args) {
    User examinee = new User(
            "examinee",
            "examinee",
            "examinee",
            false
    );
        EventQueue.invokeLater(() -> new MenuExaminee(examinee));
    }
}
