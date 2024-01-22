package View;

import DAO.EnrollmentDAO;
import Model.Enrollment;
import Model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class FinalGrade extends JFrame{
    private final User loginUser;
    private JButton backButton;
    private JTextField textFieldsearch;
    private JTable tableView;
    private JPanel panelView;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter;

    public FinalGrade(User loginUser) {
        this.loginUser = loginUser;
        this.setTitle("Final Grade");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        initComponents();
        addActionEvent();
        fillDataToTable();
        makeTableSearchable();

    }

    public static void main(String[] args) {
        var admin = new User("admin", "admin", "admin", true);
        EventQueue.invokeLater(() -> new FinalGrade(admin));
    }
    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        var i = 0;
        while ( i < columnModel.getColumnCount()){
            rowSorter.setSortable(i, false);
            ++i;
        }
        tableView.setRowSorter(rowSorter);
        textFieldsearch
                .getDocument()
                .addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        var text = textFieldsearch.getText().strip();
                        if ( text.length() != 0){
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        }else{
                            rowSorter.setRowFilter(null);
                        }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        var text = textFieldsearch.getText().strip();
                        if ( text.length() != 0){
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

    private void fillDataToTable() {
        List<Enrollment> list = EnrollmentDAO.selectAll();
        rowModel.setRowCount(0);
        for (var enrollment : list){
            rowModel.addRow(new Object[]{
                    enrollment.getUser_id(),
                    enrollment.getRoom_id(),
                    enrollment.getScore()
            });
        }
    }

    private void addActionEvent() {
        backButton.addActionListener(event ->{
            this.dispose();
            new ExaminationRoomManagement(loginUser);
        });
    }

    private void initComponents() {
        tableView.setDefaultEditor(Object.class, null);
        tableView.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"User ID", "Room ID", "Score"}
        );
        tableView.setModel(columnModel);
        rowModel = (DefaultTableModel) tableView.getModel();

    }
}
