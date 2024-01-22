package View;

import DAO.RoomDAO;
import DAO.TakeExamDAO;
import Model.User;

import javax.swing.*;

public class EnterExamRoom extends JFrame{
    private final User loginUser;
    private JCheckBox showPaswordCheckBox;
    private JButton enterButton;
    private JButton backButton;
    private JTextField textFieldRoomID;
    private JTextField textFieldpassword;
    private JPanel panelView;
    private JPasswordField passwordField;

    public EnterExamRoom(User loginUser) {
        this.loginUser = loginUser;
        addActionEvent();
        this.setTitle("Enter Examination Room");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addActionEvent() {
        showPaswordCheckBox.addActionListener(event ->{
            if (showPaswordCheckBox.isSelected()){
                passwordField.setEchoChar((char) 0);
            }else{
                passwordField.setEchoChar('*');
            }
        });
        enterButton.addActionListener(event ->{
            var roomID = textFieldRoomID.getText().strip();
            var password = String.valueOf(passwordField.getPassword());
            if ( roomID.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(
                        this,
                        "The Exam Room ID or Password cannot be left blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            var room = RoomDAO.selectVerifiedRoom(roomID, password);
            if (room == null){
                JOptionPane.showMessageDialog(
                        this,
                        "Wrong Exam Room ID or Password !",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                passwordField.setText("");
                return;
            }
            var isExamAvailable = room.getExam_id();
            if (isExamAvailable == 0 ){
                JOptionPane.showMessageDialog(
                        this,
                        "Cannot enter the room. The Exam has an error!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                passwordField.setText("");
                return;
            }
            var verifyUserAlreadyTakenExam = TakeExamDAO.verifyUserAlreadyTakenExam(
                    loginUser.getUser_id(),
                    room.getRoom_id()
            );
            if (verifyUserAlreadyTakenExam){
                JOptionPane.showMessageDialog(
                        this,
                        "Cannot enter the room. You took the exam in this room!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                passwordField.setText("");
                return;
            }
            var room_id_text = "Room ID" +room.getRoom_id();
            var room_title_text = "Title : " +room.getTitle();
            var room_timelimit_text ="Time"+room.getTime_limit();
            var confirm = "Do you want to enter the exam room now?";
            var selection = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{room_id_text,room_title_text,room_timelimit_text,confirm},
                    null,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if ( selection == JOptionPane.OK_OPTION){
                this.dispose();
               new TakeExam(loginUser,room);
            }
        });
        backButton.addActionListener(event ->{
            this.dispose();
            new MenuExaminee(loginUser);
        });
    }
    private void createUIComponents() {
    }

}
