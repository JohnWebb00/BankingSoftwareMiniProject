package crushers.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


import crushers.App;
import crushers.model.PaymentAccount;
import crushers.model.SavingsAccount;
import crushers.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AccountController implements Initializable{
    
    private Stage stage;
    private Parent root;

    @FXML
    private ListView<PaymentAccount> accountList;
    @FXML
    private Button createNewAccountButton, selectButton, logoutButton;
    @FXML
    private Label welcomeLabel, accountTypeLabel, accountNameLabel, accountBalanceLabel, savingsGoalLabel, accountIDLabel, accountBankLabel;
    @FXML
    private VBox accountDetailsBox;

    public void displayName(String username){
        welcomeLabel.setText("Welcome, " + username);
    }

    public void logout(ActionEvent e) throws IOException{
        
        Stage oldStage = (Stage)((Node)e.getSource()).getScene().getWindow();
        oldStage.close();

        stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("crushers/views/MainView.fxml"));
        root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
        stage.setTitle("Crushers Bank");
        stage.getIcons().add(new Image("crushers/imgs/logo.jpg"));
    }

    public void createNewAccount(ActionEvent e) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("crushers/views/AccountCreationView.fxml"));
        root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image("crushers/imgs/logo.jpg"));
        stage.setTitle("Register new account");
        stage.show();
    }

    public void select(ActionEvent e){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountList.getItems().addAll(App.currentCustomer.getAccountList());
        accountDetailsBox.setVisible(false);

        accountList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PaymentAccount>(){

            @Override
            public void changed(ObservableValue<? extends PaymentAccount> observable, PaymentAccount oldValue, PaymentAccount newValue) {               
                    PaymentAccount account = accountList.getSelectionModel().getSelectedItem();
                    accountDetailsBox.setVisible(true);
                    accountBankLabel.setText("Bank: " + account.getBank().toString());
                    accountTypeLabel.setText("Account type: " + account.getType());
                    accountNameLabel.setText("Account name: " + account.getName());
                    accountBalanceLabel.setText("Account balance: " + account.getBalance() + " SEK");
                    if(account.getType() == "Savings"){
                        savingsGoalLabel.setText("Savings goal: " + ((SavingsAccount)account).getSavingsGoal() + " SEK");
                        savingsGoalLabel.setVisible(true);
                    }else{
                        savingsGoalLabel.setText("");
                        savingsGoalLabel.setVisible(false);
                    }
                    accountIDLabel.setText("Account ID: " + account.getID());
            }
            
        });

    }

    public void addSavingsToList(SavingsAccount account){
        accountList.getItems().add(account);
    }
    public void addPaymentToList(PaymentAccount account){
        accountList.getItems().add(account);
    }
    public void addAccountToList(PaymentAccount account){
        accountList.getItems().add(account);
    }
    public void displayDetails(){
        // System.out.println("displaying stuff");
        // accountDetailsBox.setVisible(true);
        // // accountTypeLabel.setText()
    }
}
