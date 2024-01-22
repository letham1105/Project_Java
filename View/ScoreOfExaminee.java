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

public class ScoreOfExaminee extends JFrame {
    private final User loginUser;
    private JTextField textFieldSearch;
    private JButton backButton;
    private JTable tableView;
    private JLabel LabelSearch;
    private JPanel panelView;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter;
    public ScoreOfExaminee(User loginUser) {
        this.loginUser = loginUser;
        this.setTitle("Score of Examinee");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        var examinee = new User("examinee", "examinee", "examinee", false);
        EventQueue.invokeLater(() -> new ScoreOfExaminee(examinee));

    }

    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        var i = 0;
        while ( i < columnModel.getColumnCount()){
            rowSorter.setSortable(i, false);
            ++i;
        }
        tableView.setRowSorter(rowSorter);
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
        List<Enrollment> list = EnrollmentDAO.selectByUserID(loginUser.getUser_id());
        rowModel.setRowCount(0);
        for ( var enrollment : list){
            rowModel.addRow(new Object[]{
                    enrollment.getRoom_id(),
                    enrollment.getScore()
            });
        }
    }

    private void addActionEvent() {
        backButton.addActionListener(event ->{
            this.dispose();
            new MenuExaminee(loginUser);
        });
    }

    private void initComponents() {
        tableView.setDefaultEditor(Object.class, null);
        tableView.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Room ID", "Score"
                }
        );
        tableView.setModel(columnModel);
        rowModel = (DefaultTableModel)  tableView.getModel();
    }
}