package View;

import DAO.ExamDAO;
import Model.Exam;
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

public class ExamManagement extends JFrame {
    private final User loginUser;
    private JTextField textFieldExamID;
    private JTextField textFieldSubject;
    private JTextField textFieldTotalQuestion;
    private JTextField textFieldTotalScore;
    private JButton addButton;
    private JButton updatedButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;
    private JTextField textFieldFind;
    private JTable tableViewExam;
    private JLabel ExamIDLabel;
    private JLabel subjectLabel;
    private JLabel totalQuestionLabel;
    private JLabel totalSoreLabel;
    private JLabel searchLabel;
    private JPanel panelViewExam;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter = null;
    private List<Exam> list;
    private Exam chosenExam = null;

    public ExamManagement(User user) {
        this.loginUser = user;
        initComponents();
        addActionEvent();
        this.setTitle("Exam Management");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelViewExam);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        fillDataToTable();
        makeTableSearchable();

    }

    public static void main(String[] args) {
        User admin = new User("admin", "admin", " admin", true);
        EventQueue.invokeLater(() -> new ExamManagement(admin));
    }

    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        var i = 0;
        while (i < columnModel.getColumnCount()){
            rowSorter.setSortable(i, false);
            ++i;
        }
        tableViewExam.setRowSorter(rowSorter);
        textFieldFind
                .getDocument()
                .addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        var text = textFieldFind.getText().strip();
                        if ( text.length() != 0){
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        }else{
                            rowSorter.setRowFilter(null);
                        }
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        var text = textFieldFind.getText().strip();
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
        list = ExamDAO.selectAll();
        rowModel.setRowCount(0);
        for ( var exam : list){
            rowModel.addRow(new Object[]{
                    exam.getExam_id(),
                    exam.getSubject(),
                    exam.getTotal_question(),
                    exam.getTotal_score(),
                    exam.getScore_per_question()
            });
        }
    }

    private void addActionEvent() {
        tableViewExam.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableViewExamMouseClicked();
            }

            private void tableViewExamMouseClicked() {
                resetInputField();
                textFieldExamID.setEnabled(false);
                var index = tableViewExam.getSelectedRow();
                chosenExam = list.get(index);
                textFieldExamID.setText(String.valueOf(chosenExam.getExam_id()));
                textFieldSubject.setText(chosenExam.getSubject());
                textFieldTotalQuestion.setText(String.valueOf(chosenExam.getTotal_question()));
                textFieldTotalScore.setText(String.valueOf(chosenExam.getTotal_score()));
            }
        });
        addButton.addActionListener(event -> {
            if ( textFieldSubject.getText().isEmpty()
            || textFieldTotalQuestion.getText().isEmpty()
            || textFieldTotalScore.getText().isEmpty()){
                JOptionPane.showMessageDialog(
                        this,
                        "Information can not be blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }else{
                var subject = textFieldSubject.getText().strip();
                var totalQuestion = Integer.parseInt(textFieldTotalQuestion.getText().strip());
                var totalScore = Integer.parseInt(textFieldTotalScore.getText().strip());
                var scorePerQuestion = totalScore / ( double) totalQuestion;
                var exam = new Exam(subject, totalQuestion, totalScore, scorePerQuestion);
                var isSuccess = ExamDAO.insert(exam);
                if ( isSuccess){
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
            }
        });
        updatedButton.addActionListener(event -> {
            if ( textFieldExamID.getText().isEmpty()
                    || textFieldSubject.getText().isEmpty()
                    ||textFieldTotalQuestion.getText().isEmpty()
                    || textFieldTotalScore.getText().isEmpty()){
                JOptionPane.showMessageDialog(
                        this,
                        "Information can not be blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }else{
                var exam_id = Long.parseLong(textFieldExamID.getText().strip());
                var subject = textFieldSubject.getText().strip();
                var totalQuestion = Integer.parseInt(textFieldTotalQuestion.getText().strip());
                var totalScore = Integer.parseInt(textFieldTotalScore.getText().strip());
                var scorePerQuestion = totalScore / (double) totalQuestion;
                var exam = new Exam(exam_id, subject, totalQuestion, totalScore, scorePerQuestion);
                var isSuccess = ExamDAO.update(exam);
                if ( isSuccess ){
                    JOptionPane.showMessageDialog(
                            this,
                            "Update Success",
                            "Warning",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    fillDataToTable();
                }else{
                    JOptionPane.showMessageDialog(
                            this,
                            "Add failure. Please try again !"
                    );
                }
                resetInputField();
            }
        });
        deleteButton.addActionListener(event ->{
            var examID = textFieldExamID.getText().strip();
            if ( !examID.isEmpty()){
                var isSuccess = ExamDAO.delete(Long.parseLong(examID));
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
                        "Please select the Exam you want to delete !",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
        refreshButton.addActionListener(event ->{
            resetInputField();
            textFieldFind.setText("");
        });
        backButton.addActionListener(event ->{
            if ( loginUser.getUser_id().equals("admin")){
                this.dispose();
                new MenuAdmin(loginUser);
            }else{
                this.dispose();
                new MenuExaminationSetter(loginUser);
            }
        });
    }

    private void resetInputField() {
        textFieldExamID.setText("");
        textFieldSubject.setText("");
        textFieldTotalQuestion.setText("");
        textFieldTotalScore.setText("");

    }

    private void initComponents() {
        textFieldExamID.setEnabled(false);
        tableViewExam.setDefaultEditor(Object.class, null);
        tableViewExam.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[] {
                        "Exam ID", "Subject", "Total Question", "Total Result", "Score per Question"
                }
        );
        tableViewExam.setModel(columnModel);
        rowModel = (DefaultTableModel) tableViewExam.getModel();

    }
}
