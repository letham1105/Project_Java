package View;

import DAO.ExamDAO;
import DAO.RoomDAO;
import Model.Room;
import Model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ExaminationRoomManagement extends JFrame{
    private final User loginUser;
    private JTextField textFieldRoomID;
    private JRadioButton openRadioButton;
    private JButton addButton;
    private JButton updatedButton;
    private JButton deleteButton;
    private JButton finalGradeButton;
    private JButton refreshButton;
    private JButton backButton;
    private JTable tableviewRoom;
    private JTextField textFieldSearch;
    private JTextField textFieldExamID;
    private JTextField textFieldTitle;
    private JTextField textFieldRoomPassword;
    private ButtonGroup buttongroupStatusViewRoomManagement;
    private JRadioButton closeRadioButton;
    private JLabel LabelRoomID;
    private JLabel LabelExamID;
    private JLabel LabelTitle;
    private JLabel LabelRoomPassword;
    private JLabel labelstate;
    private JLabel LabelSearch;
    private JPanel panelViewRoom;
    private JTextField textFieldTimeLimit;
    private JLabel LabelTimeLimit;
    private JScrollPane buttonGroupStatus;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter = null;
    private List<Room> list;
    private Room chosenRoom = null;


    public ExaminationRoomManagement(User loginUser) {
        this.loginUser = loginUser;
         initComponents();
         addActionEvent();
         this.setTitle("Examination Room Management");
         this.setResizable(false);
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         this.setContentPane(panelViewRoom);
         this.pack();
         this.setLocationRelativeTo(null);
         this.setVisible(true);
         fillDataToTable();
         makeTableSearchable();
    }

    public static void main(String[] args) {
        User admin = new User("admin", "admin","admin",true);
        EventQueue.invokeLater(() ->
            new ExaminationRoomManagement(admin)
        );
    }

    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        var i = 0;
        while ( i < columnModel.getColumnCount()){
            rowSorter.setSortable(i, false);
            ++i;
        }
        tableviewRoom.setRowSorter(rowSorter);
        textFieldSearch
                .getDocument()
                .addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        var text = textFieldSearch.getText().strip();
                        if ( text.length() != 0){
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        }else{
                            rowSorter.setRowFilter(null);
                        }
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        var text = textFieldSearch.getText().strip();
                        if ( text.length() != 0 ){
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        }else{
                            rowSorter.setRowFilter(null);
                        }
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {

                    }
                });
    }


    private void addActionEvent() {
        tableviewRoom.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               tableviewRoomMouseClicked();
            }

            private void tableviewRoomMouseClicked() {
                resetInputField();
                textFieldRoomID.setEnabled(false);
                var index = tableviewRoom.getSelectedRow();
                chosenRoom = list.get(index);
                textFieldRoomID.setText(String.valueOf(chosenRoom.getRoom_id()));
                textFieldExamID.setText(
                        String.valueOf((chosenRoom.getExam_id() == 0) ? "null" : chosenRoom.getExam_id())
                );
                textFieldTitle.setText(chosenRoom.getTitle());
                textFieldTimeLimit.setText(String.valueOf(chosenRoom.getTime_limit()));
                textFieldRoomPassword.setText(chosenRoom.getPassword());
                if (chosenRoom.isAvailable()){
                    openRadioButton.setSelected(true);
                }else{
                    closeRadioButton.setSelected(true);
                }
            }
        });
        addButton.addActionListener(event ->{
            var radioOpen = openRadioButton.isSelected();
            var radioClose = closeRadioButton.isSelected();
            var radioIsSelected = radioOpen || radioClose;
            if (textFieldExamID.getText().isEmpty()
                    || textFieldTitle.getText().isEmpty()
                    || textFieldTimeLimit.getText().isEmpty()
                    || textFieldRoomPassword.getText().isEmpty()
                    || !radioIsSelected) {
                JOptionPane.showMessageDialog(
                        this,
                        "Information can not be blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            var exam_id = Long.parseLong(textFieldExamID.getText().strip());
            var checkValidExamID = ExamDAO.selectByID(exam_id);
            if ( checkValidExamID == null){
                JOptionPane.showMessageDialog(
                        this,
                        "The Exam ID does not exist. Please check and try again later!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            var title = textFieldTitle.getText().strip();
            var time_limit = Integer.parseInt(textFieldTimeLimit.getText().strip());
            var password = textFieldRoomPassword.getText().strip();
            var is_available = openRadioButton.isSelected();
            var room = new Room(exam_id, title, time_limit, password, is_available);
            var isSuccess = RoomDAO.insert(room);
            if ( isSuccess ){
                JOptionPane.showMessageDialog(
                        this,
                        "Add Success",
                        "Notice",
                        JOptionPane.INFORMATION_MESSAGE
                );
                fillDataToTable();
            }else{
                JOptionPane.showMessageDialog(
                        this,
                        "Add failure. Please try again !",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            resetInputField();
        });
        updatedButton.addActionListener(event ->{
            var radioOpen = openRadioButton.isSelected();
            var radioClose = closeRadioButton.isSelected();
            var radioIsSelected = radioOpen || radioClose;
            if ( textFieldRoomID.getText().isEmpty()
                    || textFieldExamID.getText().isEmpty()
                    || textFieldTitle.getText().isEmpty()
                    || textFieldTimeLimit.getText().isEmpty()
                    || textFieldRoomPassword.getText().isEmpty()
                    || !radioIsSelected
               ){
                JOptionPane.showMessageDialog(
                        this,
                        "Information can not be blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            var room_id = Long.parseLong(textFieldRoomID.getText().strip());
            var exam_id = Long.parseLong(textFieldExamID.getText().strip());
            var checkValidExamID = ExamDAO.selectByID(exam_id);
            if ( checkValidExamID == null){
                JOptionPane.showMessageDialog(
                        this,
                        "The Exam ID does not exist. Please check and try again later!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            var title = textFieldTitle.getText().strip();
            var time_limit = Integer.parseInt(textFieldTimeLimit.getText().strip());
            var password = textFieldRoomPassword.getText().strip();
            var is_available = openRadioButton.isSelected();
            var room = new Room(room_id,exam_id, title, time_limit,password,is_available);
            var isSuccess = RoomDAO.update(room);
            if (isSuccess){
                JOptionPane.showMessageDialog(
                        this,
                        "Update Success",
                        "Notice",
                        JOptionPane.INFORMATION_MESSAGE
                );
                fillDataToTable();
            }else {
                JOptionPane.showMessageDialog(
                        this,
                        "Update failure. Please try again !",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            resetInputField();
        });
        deleteButton.addActionListener(event ->{
            var roomID = textFieldRoomID.getText().strip();
            if ( !roomID.isEmpty() ){
                var isSuccess = RoomDAO.delete(Long.parseLong(roomID));
                if (isSuccess){
                    JOptionPane.showMessageDialog(
                            this,
                            "Delete Success",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    fillDataToTable();
                }else{
                    JOptionPane.showMessageDialog(
                            this,
                            "Delete failure. Please try again !",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                resetInputField();
            }else{
                JOptionPane.showMessageDialog(
                        this,
                        "Please select the room you want to delete !",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
        finalGradeButton.addActionListener(event -> {
            this.dispose();
            new FinalGrade(loginUser);
        });
    refreshButton.addActionListener(event ->{
        resetInputField();
        textFieldSearch.setText("");
    });
    backButton.addActionListener(event ->{
        if (loginUser.getUser_id().equals("admin")){
            this.dispose();
            new MenuAdmin(loginUser);
        }else{
            this.dispose();
            new MenuExaminationSetter(loginUser);
        }
    });
    }


    private void fillDataToTable() {
        list = RoomDAO.selectAll();
        rowModel.setRowCount(0);
        for ( var room : list){
            rowModel.addRow(new Object[]{
                    room.getRoom_id(),
                    (room.getExam_id() == 0) ? "null" : room.getExam_id(),
                    room.getTitle(),
                    room.getTime_limit(),
                    room.getPassword(),
                    room.isAvailable()
            });
        }
    }

    private void resetInputField() {
        textFieldRoomID.setText("");
        textFieldExamID.setText("");
        textFieldTitle.setText("");
        textFieldTimeLimit.setText("");
        textFieldRoomPassword.setText("");
      //  buttongroupStatusViewRoomManagement.clearSelection();
    }

    private void initComponents() {
        textFieldRoomID.setEnabled(false);
        tableviewRoom.setDefaultEditor(Object.class, null);
        tableviewRoom.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Room ID", "Exam ID", "Title", "Time", "Password Room", "State"
                }
        );
        tableviewRoom.setModel(columnModel);
        rowModel = (DefaultTableModel) tableviewRoom.getModel();
    }

}
