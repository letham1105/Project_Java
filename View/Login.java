package View;

import DAO.UserDAO;
import Model.User;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private JTextField textFieldUserName;
    private JCheckBox showPasswordCheckBox;
    private JButton exitButton;
    private JButton logInButton;
    private JButton signUpButton;
    private JPasswordField passwordField1;
    private JPanel panelView;
    private static final String username_admin = "admin";
    private static final String password_admin = "admin";

    public Login(){
        addActionEvent();
        this.setTitle("Log in");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(Login::new);
    }

    private void addActionEvent() {
        logInButton.addActionListener(event ->{
            var username = textFieldUserName.getText().strip();
            var password = String.valueOf(passwordField1.getPassword()).strip();
            if ( username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(
                        this,
                        "Information cannot be left blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }else {
                if (username.equals(username_admin) && password.equals(password_admin)) {
                    var admin = new User(username_admin, username_admin, password_admin, true);
                    this.dispose();
                    new MenuAdmin(admin);
                } else {
                    var password_encrypted = UserDAO.encryptPassword(password);
                    var loginUser = UserDAO.selectByAccount(username, password_encrypted);
                    if (loginUser != null) {
                        var checkHost = loginUser.isHost();
                        if (checkHost) {
                            new MenuExaminationSetter(loginUser);
                        } else {
                            new MenuExaminee(loginUser);
                        }
                    }else{
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Wrong login name or password",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE
                            );
                            passwordField1.setText("");
                        }
                    }
                }
            });
                    signUpButton.addActionListener(even ->{
                        this.dispose();
                        new SignUp();
                });
                    showPasswordCheckBox.addActionListener(event ->{
                    if ( showPasswordCheckBox.isSelected()){
                        passwordField1.setEchoChar((char) 0);
                    }else{
                        passwordField1.setEchoChar('*');
                    }
                });
                    exitButton.addActionListener(event ->{
                        var selection = JOptionPane.showConfirmDialog(
                                this,
                                "Do you really want to exit",
                                "Exit",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if ( selection == JOptionPane.OK_OPTION){
                            System.exit(0);
                        }
                });
            }
            private void createUIComponents(){
                }

}

