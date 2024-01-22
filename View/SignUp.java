package View;

import DAO.UserDAO;
import Model.User;

import javax.swing.*;
import java.awt.*;

public class SignUp extends JFrame {
    private JTextField textFieldUserID;
    private JTextField textFieldFullName;
    private JButton backButton;
    private JButton signUpButton;
    private JLabel LabelUserID;
    private JLabel LabelFullName;
    private JPasswordField passwordField1;
    private JPasswordField ConfirmpasswordField2;
    private JPanel panelView;

    public SignUp(){
        addActionEvent();
        this.setTitle("Sign Up");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
}

    public static void main(String[] args) {
        EventQueue.invokeLater(SignUp ::new);
    }

    private void addActionEvent() {
        signUpButton.addActionListener(even ->{
            var userID = textFieldUserID.getText().strip();
            var fullName = textFieldFullName.getText().strip();
            var password = String.valueOf(passwordField1.getPassword()).strip();
            var passwordAgain = String.valueOf(ConfirmpasswordField2.getPassword()).strip();
            if ( userID.isEmpty() || fullName.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()){
                JOptionPane.showMessageDialog(
                        this,
                        "Information cannot be left blank!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                textFieldUserID.setText("");
                passwordField1.setText("");
                ConfirmpasswordField2.setText("");
            }else if ( verifyAccountNoExits(userID)){
                var user = new User(userID, fullName, password, false);
                var isSuccess = UserDAO.insert(user);
                if ( isSuccess){
                    JOptionPane.showMessageDialog(
                            this,
                            "Sign up Success",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }else {
                    JOptionPane.showMessageDialog(
                            this,
                            " Account Sign-up failed. Please try again!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                resetAll();
            }else{
                JOptionPane.showMessageDialog(
                        this,
                        "UserID already exists, try again with another UserID!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                passwordField1.setText("");
                ConfirmpasswordField2.setText("");
            }
        });
        backButton.addActionListener(event ->{
            this.dispose();
            new Login();
        });
    }
    private boolean verifyAccountNoExits( String usetID){
        var user = UserDAO.selectByID(usetID);
        return user == null;
    }

    private void resetAll() {
        textFieldUserID.setText("");
        textFieldFullName.setText("");
        passwordField1.setText("");
        ConfirmpasswordField2.setText("");
    }
    private void createUIComponents(){
    }
}
