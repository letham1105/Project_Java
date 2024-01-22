package View;

import DAO.UserDAO;
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

public class UserManagement extends JFrame{
    private final User loginUser;
    private JPanel panelviewUserManagement;
    private JLabel labelUserID;
    private JLabel labelFullName;
    private JLabel labelPassword;
    private JLabel labelUserRole;
    private JTextField textfieldFullName;
    private JTextField textfieldUserID;
    private JPasswordField textfieldPassword;
    private JRadioButton radioButtonExaminationSetter;
    private JRadioButton radioexaminee;
    private JCheckBox checkboxChangePassword;
    private JButton deleteButton;
    private JButton updatedButton;
    private JButton addButton;
    private JButton backButton;
    private JButton refreshButton;
    private JLabel labelFind;
    private JTextField textfieldFind;
    private JTable tableView;
    private List<User> list;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter = null;
    private String passwordBeforeChanged;
    private ButtonGroup buttonGroup;
    private User chosenUser = null;

    public UserManagement(User user) {
        this.loginUser = user;
        initComponents();
        addActionEvent();
        this.setTitle("User Management");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelviewUserManagement);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        fillDataToTable();
        makeTableSearchable();
    }

    private void initComponents() {
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonExaminationSetter);
        buttonGroup.add( radioexaminee);
        tableView.setDefaultEditor(Object.class, null);
        tableView.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"UserID", "Name", "Password", "Host"}
        );
        tableView.setModel(columnModel);
        rowModel = (DefaultTableModel) tableView.getModel();
    }
    private void addActionEvent() {
        tableView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableViewUserMouseClicked();
            }


            private void tableViewUserMouseClicked() {
                resetInputField();
                textfieldUserID.setEnabled(false);
                checkboxChangePassword.setSelected(false);
                textfieldPassword.setEnabled(false);
                var index = tableView.getSelectedRow();
                chosenUser = list.get(index);
                passwordBeforeChanged = chosenUser.getPassword();
                textfieldUserID.setText(chosenUser.getUser_id());
                textfieldFullName.setText(chosenUser.getFull_name());
                textfieldPassword.setText(passwordBeforeChanged);
                if (chosenUser.isHost()) {
                    radioButtonExaminationSetter.setSelected(true);
                } else {
                    radioexaminee.setSelected(true);
                }
            }
        });
        checkboxChangePassword.addActionListener(e ->
                textfieldPassword.setEnabled(checkboxChangePassword.isSelected())
        );
        addButton.addActionListener(event -> {
            var userID = textfieldUserID.getText().strip();
            var fullname = textfieldFullName.getText().strip();
            var password = textfieldPassword.getText().strip();
            var radioHost = radioButtonExaminationSetter.isSelected();
            var radioExaminee = radioexaminee.isSelected();
            var radioIsSeleted = radioHost || radioExaminee;
            if (userID.isEmpty() || fullname.isEmpty() || password.isEmpty() || !radioIsSeleted) {
                JOptionPane.showMessageDialog(
                        this,
                        "Information cannot be left blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            } else if (userID.equals("admin")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to add account with this UserID. Please use another UserID!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                textfieldUserID.setText("");
                textfieldPassword.setText("");
            } else if (verifyAccountNotExits(userID)) {
                var user = new User(userID, fullname, password, radioHost);
                var isSuccess = UserDAO.insert(user);
                if (isSuccess) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Add Success",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    fillDataToTable();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Add failure. Please try again !",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                resetInputField();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "UserID already exists, try again with another UserID!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
        updatedButton.addActionListener(event -> {
            var userID = textfieldUserID.getText().strip();
            var fullName = textfieldFullName.getText().strip();
            var password = textfieldPassword.getText().strip();
            var radioHost = radioButtonExaminationSetter.isSelected();
            var radioExaminee = radioexaminee.isSelected();
            var radioIsSelected = radioHost || radioExaminee;
            if (userID.isEmpty() || fullName.isEmpty() || password.isEmpty() || !radioIsSelected) {
                JOptionPane.showMessageDialog(
                        this,
                        "Information cannot be left blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                var passwordIsChanged = !password.equals(passwordBeforeChanged);
                var password_encrypted = (passwordIsChanged) ? UserDAO.encryptPassword(password) : passwordBeforeChanged;
                var user = new User(
                        userID,
                        fullName,
                        password_encrypted,
                        radioHost
                );
                var isSuccess = UserDAO.update(user);
                if (isSuccess) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Update Success !",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    fillDataToTable();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Update failure. Please try again !",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                resetInputField();
            }
        });
        deleteButton.addActionListener(event -> {
            var userID = textfieldUserID.getText().strip();
            if (!userID.isEmpty()) {
                var isSuccess = UserDAO.delete(userID);
                if (isSuccess) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Delete Success.",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    fillDataToTable();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Delete failure. Please try again !",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                resetInputField();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select the account you want to delete !",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
        refreshButton.addActionListener(event -> {
            resetInputField();
            textfieldFind.setText("");
        });
        backButton.addActionListener(event -> {
            if (loginUser.getUser_id().equals("admin")) {
                this.dispose();
                new MenuAdmin(loginUser);
            } else {
                this.dispose();
                new MenuExaminationSetter(loginUser);
            }
        });
    }
    private void fillDataToTable() {
        list = UserDAO.selectAll();
        rowModel.setRowCount(0);
        for (var user : list) {
            rowModel.addRow(new Object[]{
                    user.getUser_id(),
                    user.getFull_name(),
                    user.getPassword(),
                    user.isHost()
            });
        }
    }
    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        var i = 0;
        while (i < columnModel.getColumnCount()) {
            rowSorter.setSortable(i, false);
            ++i;
        }
        tableView.setRowSorter(rowSorter);
        textfieldFind
                .getDocument()
                .addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        var text = textfieldFind.getText().strip();
                        if (text.length() != 0) {
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        } else {
                            rowSorter.setRowFilter(null);
                        }
                    }


                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        var text = textfieldFind.getText().strip();
                        if (text.length() != 0) {
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        } else {
                            rowSorter.setRowFilter(null);
                        }
                    }


                    @Override
                    public void changedUpdate(DocumentEvent e) {
                    }
                });
    }
    private void resetInputField() {
        textfieldUserID.setText("");
        textfieldUserID.setEnabled(true);
        textfieldFullName.setText("");
        textfieldPassword.setText("");
        buttonGroup.clearSelection();
        checkboxChangePassword.setSelected(true);
    }
    private boolean verifyAccountNotExits(String userID) {
        var user = UserDAO.selectByID(userID);
        return user == null;
    }
    private void createUIComponents() {
    }

}


