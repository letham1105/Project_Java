package View;

import DAO.EnrollmentAnswerDAO;
import DAO.EnrollmentDAO;
import DAO.RoomDAO;
import DAO.TakeExamDAO;
import Model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TakeExam extends JFrame{
    private final User loginUser;
    private final Room room;
    private JPanel panelView;
    private JTabbedPane tabbedpanelTakeExam;
    private JLabel labelCountDownClock;
    private JButton buttonSubmit;
    private JLabel labelRoomID;
    private JLabel labelDataRoomID;
    private JLabel labelTimeLimit;
    private JLabel labelDataTimeLimit;
    private JLabel labelTotalQuestion;
    private JLabel labelDataTotalQuestion;
    private JLabel labelRoomTitle;
    private Exam exam;

    private List<Question> questionList;

    private List<List<QuestionAnswer>> listOfQuestionAnswerList;

    private List<QuestionAndAnswer> questionAndAnswerFormList;

    private int prevQAAFormIndex;

    private Timer timer;

    private List<String> correctAnswerList;

    private List<String> chosenAnswerList;

    private List<String> resultList;

    private int totalCorrect;

    private double score;
    public TakeExam (User loginUser, Room room){
        this.loginUser = loginUser;
        this.room = room;
        this.setTitle("Examination Room");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        initComponents();
        addActionEvent();
        setTimer();
}

    public static void main(String[] args) {
        var user = new User ( "user", "user", "user", false);
        var room = RoomDAO.selectByID(1);
        EventQueue.invokeLater(() -> new TakeExam(user,room));
    }
    private void addActionEvent() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                var frame = (JFrame ) e.getSource();
                var selection = JOptionPane.showConfirmDialog(
                        frame,
                        new Object[]{
                                "Your exam results will still be counted if you exit now!",
                                "Do you want to exit the exam room?"
                        },
                        "Confirm",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (selection == JOptionPane.OK_OPTION){
                    timer.stop();
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    executeAndShowResult();
                }
            }
        });
        tabbedpanelTakeExam.addChangeListener(event ->{
            var prevQAAForm = (QuestionAndAnswer) tabbedpanelTakeExam.getComponentAt(prevQAAFormIndex);
            if (prevQAAForm.getButtonGroup().getSelection() != null){
                tabbedpanelTakeExam.setForegroundAt(prevQAAFormIndex, Color.WHITE);
                tabbedpanelTakeExam.setBackgroundAt(prevQAAFormIndex, Color.BLUE);
            }else{
                tabbedpanelTakeExam.setForegroundAt(prevQAAFormIndex, null);
                tabbedpanelTakeExam.setBackgroundAt(prevQAAFormIndex, null);
            }
        });
        buttonSubmit.addActionListener(event ->{
            var selection = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to submit your exam?",
                    "Confirm",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                    );
            if ( selection == JOptionPane.OK_OPTION){
                timer.stop();
                this.dispose();
                executeAndShowResult();
            }
        });
    }

    private void executeAndShowResult() {
        executeExamResult();
        saveExamResultToDatabase();
        new Result(
                loginUser,
                questionList,
                chosenAnswerList,
                correctAnswerList,
                resultList,
                totalCorrect,
                 score
        );
    }
    private void executeExamResult() {
        for ( var questionAndAnswer : questionAndAnswerFormList){
            String s1;
            try {
                s1 = questionAndAnswer.getButtonGroup().getSelection().getActionCommand();
            } catch (Exception e) {
                s1 = "No Answer";
            }
            chosenAnswerList.add(s1);
        }
        var index = 0;
        for ( var chosenAnswer : chosenAnswerList){
            var correctAnswer = correctAnswerList.get(index++);
            if ( chosenAnswer.equals(correctAnswer)){
                resultList.add("Correct");
                totalCorrect++;
            }else{
                resultList.add("Wrong");
            }
        }
        score = totalCorrect * exam.getScore_per_question();
    }

    private void saveExamResultToDatabase() {
        var user_id = loginUser.getUser_id();
        var room_id = room.getRoom_id();
        Enrollment enrollment = new Enrollment(user_id, room_id, score);
        var isSuccess = EnrollmentDAO.insert(enrollment);
        if ( isSuccess){
            var enrollment_id = EnrollmentDAO.selectIDByModel(enrollment);
            var index = 0;
            var enrollmentAnswer = new EnrollmentAnswer();
            for ( var questionAnswerList : listOfQuestionAnswerList){
                for ( var questionAnswer : questionAnswerList){
                    var chosenAnswer = chosenAnswerList.get(index);
                    if ( chosenAnswer.contains("No Answer")){
                        enrollmentAnswer = new EnrollmentAnswer(
                                enrollment_id,
                                questionAnswer.getQuestion_id(),
                                null
                        );
                    }
                    if ( questionAnswer.getContent().equals(chosenAnswer)){
                        enrollmentAnswer = new EnrollmentAnswer(
                                enrollment_id,
                                questionAnswer.getQuestion_id(),
                                questionAnswer.getQuestion_answer_id()
                        );
                    }
                }
                EnrollmentAnswerDAO.insert(enrollmentAnswer);
                index++;
            }
        }else{
            JOptionPane.showMessageDialog(
                    this,
                    "Save failed exam results",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }



    private void initComponents() {
        exam = TakeExamDAO.selectExamOfRoom(room.getRoom_id());
        if ( exam == null){
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot enter the room. The Exam has an error!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            this.dispose();
            new MenuExaminee(loginUser);
            return;
        }
        questionList = TakeExamDAO.selectQuestionOfExam(exam.getExam_id());
        if ( questionList.size() == 0){
            JOptionPane.showMessageDialog(
                    this,
                    "The exam has no questions. You will automatically exit the room!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            this.dispose();
            new MenuExaminee(loginUser);
            return;
        }
        labelDataTotalQuestion.setText(String.valueOf(exam.getTotal_question()));
        labelRoomTitle.setText(room.getTitle().strip());
        labelDataRoomID.setText(String.valueOf(room.getRoom_id()));
        labelDataTimeLimit.setText(String.valueOf(room.getTime_limit()));
        listOfQuestionAnswerList = new ArrayList<>();
        correctAnswerList = new ArrayList<>();
        chosenAnswerList = new ArrayList<>();
        questionAndAnswerFormList = new ArrayList<>();
        resultList = new ArrayList<>();
        prevQAAFormIndex = 0;
        totalCorrect = 0;
        fillDataToAAForm();
    }

    private void fillDataToAAForm() {
        var index = 1;
        for ( var question : questionList){
            List<QuestionAnswer> questionAnswerList = TakeExamDAO.selectQuestionAnswerOfQuestion(question.getQuestion_id());
            var questionAndAnswer = new QuestionAndAnswer(question, questionAnswerList);
            tabbedpanelTakeExam.addTab(String.valueOf(index++), questionAndAnswer);
            for ( var questionAnswer : questionAnswerList){
                if  ( questionAnswer.isCorrect()){
                    correctAnswerList.add(questionAnswer.getContent());
                }
            }
            questionAndAnswerFormList.add(questionAndAnswer);
            listOfQuestionAnswerList.add(questionAnswerList);
        }
    }
    //countdown timer
    private void setTimer(){
        timer = new Timer(1000, new ActionListener() {
            int time_limit = room.getTime_limit()*60;
            int hours = time_limit / 3600;
            int minutes = time_limit % 3600 / 60;
            int seconds = time_limit - hours * 3600 -minutes * 60;
            String h, m, s, timeFormat;
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( time_limit == 0){
                    timer.stop();
                    dispose();
                    JOptionPane.showMessageDialog(
                            panelView,
                            "Exam time is over!",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    executeAndShowResult();
                }
                if ( hours < 10){
                    h = "0" + hours;
                }else{
                    h = String.valueOf(hours);
                }
                if ( minutes < 10){
                    m = "0" + minutes;
                }else{
                    m = String.valueOf(minutes);
                }
                if (seconds < 10){
                    s = "0" + seconds;
                }else{
                    s = String.valueOf(seconds);
                }
                timeFormat = String.format("%s:%s:%s", h, m, s);
                labelCountDownClock.setText(timeFormat);
                if ( seconds == 0) {
                    seconds = 59;
                    if (minutes == 0) {
                        minutes = 59;
                        hours--;
                    } else {
                        minutes--;
                    }
                }else{
                    seconds--;
                }
                time_limit--;
            }
        });
        timer.start();
    }

}
