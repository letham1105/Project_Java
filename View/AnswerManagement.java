package View;

import DAO.QuestionAnswerDAO;
import DAO.QuestionDAO;
import Model.EnrollmentAnswer;
import Model.QuestionAnswer;
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

public class AnswerManagement extends JFrame{
    private final User loginUser;
    private JTextField textFieldQuestionID;
    private JTextField textFieldAnswer;
    private JCheckBox correctAnswerCheckBox;
    private JButton backButton;
    private JButton refreshButton;
    private JButton updatedButton;
    private JButton addButton;
    private JButton deleteButton;
    private JTextField textFieldSearch;
    private JTable tableView;
    private JTextField textFieldAnswerID;
    private JLabel LabelAnswerID;
    private JLabel labelQuestionID;
    private JLabel LabelAnswer;
    private JLabel LabelSearch;
    private JPanel panelView;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter = null;
    private List<QuestionAnswer> list;
    private QuestionAnswer chosenQuestionAnswer = null;
    public AnswerManagement (User user){
        this.loginUser = user;
        initComponents();
        addActionEvent();
        this.setTitle("Answer Management");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        fillDataToTable();
        makeTableSearchable();

        
    }

    public static void main(String[] args) {
        User admin = new User("admin", "admin", "admin", true);
        EventQueue.invokeLater(() ->
            new AnswerManagement(admin)
        );
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
        list = QuestionAnswerDAO.selectAll();
        rowModel.setRowCount(0);
        for ( var questionAnswer : list){
            rowModel.addRow(new Object[]{
                    questionAnswer.getQuestion_answer_id(),
                    questionAnswer.getQuestion_id(),
                    questionAnswer.getContent(),
                    questionAnswer.isCorrect()
            });
        }
    }

    private void addActionEvent() {
        tableView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableViewAnswerMouseClicked();
            }

            private void tableViewAnswerMouseClicked() {
                resetInputField();
                textFieldAnswerID.setEnabled(false);
                var index = tableView.getSelectedRow();
                chosenQuestionAnswer = list.get(index);
                textFieldAnswerID.setText(String.valueOf(chosenQuestionAnswer.getQuestion_answer_id()));
                textFieldQuestionID.setText(String.valueOf(chosenQuestionAnswer.getQuestion_id()));
                textFieldAnswer.setText(chosenQuestionAnswer.getContent());
                correctAnswerCheckBox.setSelected(chosenQuestionAnswer.isCorrect());
            }
        });
        addButton.addActionListener(event ->{
            if (textFieldQuestionID.getText().isEmpty()
                || textFieldAnswer.getText().isEmpty()){
            JOptionPane.showMessageDialog(
                    this,
                    "Information cannot be blank",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
            }
            var question_id = Long.parseLong(textFieldQuestionID.getText().strip());
            var checkValidQuestionID = QuestionDAO.selectByID(question_id);
            if ( checkValidQuestionID == null){
                JOptionPane.showMessageDialog(
                        this,
                        "The Question ID does not exist. Please check and try again later!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            var content = textFieldAnswer.getText().strip();
            var isCorrect = correctAnswerCheckBox.isSelected();
            var questionAnswer = new QuestionAnswer(question_id, content, isCorrect);
            var isSuccess = QuestionAnswerDAO.insert(questionAnswer);
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
        });
        updatedButton.addActionListener(event ->{
            if ( textFieldAnswerID.getText().isEmpty()
            || textFieldQuestionID.getText().isEmpty()
            || textFieldAnswer.getText().isEmpty()){
                JOptionPane.showMessageDialog(
                        this,
                        "Information can not be blank!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                        );
                return;
            }
           var question_answer_id = Long.parseLong(textFieldAnswerID.getText().strip());
            var question_id = Long.parseLong(textFieldQuestionID.getText().strip());
            var checkValidQuestionID = QuestionDAO.selectByID(question_id);
            if ( checkValidQuestionID == null){
                JOptionPane.showMessageDialog(
                        this,
                        "The Question ID does not exist. Please check and try again later!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            var content = textFieldAnswer.getText().strip();
            var isCorrect = correctAnswerCheckBox.isSelected();
            var questionAnswer = new QuestionAnswer(question_answer_id, question_id, content, isCorrect);
            var isSuccess = QuestionAnswerDAO.update(questionAnswer);
            if ( isSuccess){
                JOptionPane.showMessageDialog(
                        this,
                        "Update Success",
                        "Notice",
                        JOptionPane.INFORMATION_MESSAGE
                );
                fillDataToTable();
            }else{
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
    var questionAnswerID = textFieldAnswerID.getText().strip();
    if (!questionAnswerID.isEmpty()){
        var isSuccess = QuestionAnswerDAO.delete(Long.parseLong(questionAnswerID));
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
                "Please select the Answer you want to delete!",
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }
});
   refreshButton.addActionListener(event ->{
       resetInputField();
       LabelSearch.setText("");
   });
   backButton.addActionListener(event ->{
       this.dispose();
   });
    }

    private void resetInputField() {
        textFieldAnswerID.setText("");
        textFieldQuestionID.setText("");
        textFieldAnswer.setText("");
        correctAnswerCheckBox.setSelected(false);
    }

    private void initComponents() {
        textFieldAnswerID.setEnabled(false);
        tableView.setDefaultEditor(Object.class, null);
        tableView.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Answer ID", "Question ID", "Content", "Correct Answer"
                }
        );
        tableView.setModel(columnModel);
        rowModel = (DefaultTableModel) tableView.getModel();
    }
}
