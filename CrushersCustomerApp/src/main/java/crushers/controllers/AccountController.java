package crushers.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


import crushers.App;
import crushers.model.PaymentAccount;
import crushers.model.SavingsAccount;
import crushers.util.Http;
import crushers.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AccountController implements Initializable{
    
    private Stage stage;
    private Parent root;
    double customerTotalBalance;

    @FXML
    private ListView<PaymentAccount> accountList;
    @FXML
    private Button createNewAccountButton, selectButton, logoutButton, transferButton;
    @FXML
    private Label welcomeLabel, accountTypeLabel, accountNameLabel, totalBalanceLabel, accountBalanceLabel, savingsGoalLabel, accountIDLabel, accountBankLabel, invalidLabel;
    @FXML
    private VBox accountDetailsBox;

    public void displayName(String username){
        welcomeLabel.setText("Welcome, " + username);
    }

    public void displayTotalBalance(double totalBalance) {

    }

    public void logout(ActionEvent e) throws IOException{
        
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to log-out?");
        alert.setTitle("Logging out");
        alert.setHeaderText("");
        alert.setX(500);
        alert.setY(250);
        
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.CANCEL){
            return;
        }

        try {
            Http.authPost("auth/logout", App.currentToken).body();
            App.currentCustomer = null;
            App.currentToken = null;
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        Util.closeAndShow("MainView", "Crushers Bank", e);
    }

    public void createNewAccount(ActionEvent e) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("crushers/views/AccountCreationView.fxml"));
        root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image("crushers/imgs/logo.jpg"));
        stage.setTitle("Register new account");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void select(ActionEvent e) throws IOException{
        PaymentAccount account = accountList.getSelectionModel().getSelectedItem();
        if(account == null){
            invalidLabel.setText("Please select an account!");
        }else{
            Util.closeAndShow("SystemView", "Crushers System", e);
        }
    }

    public void transferFunds(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("crushers/views/AccountTransferView.fxml"));
        root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Transfer Funds");
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String firstName = App.currentCustomer.getFirstName();
        String lastName = App.currentCustomer.getLastName();
        double totalBalance = getCustomerTotalBalance();
        totalBalanceLabel.setText("Total Balance: " + totalBalance);
        welcomeLabel.setText("Welcome, " + firstName + " " + lastName);
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

    public double getCustomerTotalBalance(){
        if(App.currentCustomer.getAccountList().size() == 0){
            return 0;
        }else{
            ArrayList<PaymentAccount> currentCustomerAccounts = App.currentCustomer.getAccountList();
            for (PaymentAccount currentCustomerAccount : currentCustomerAccounts) {
                customerTotalBalance += currentCustomerAccount.getBalance();
            }
        return customerTotalBalance;
        }
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
}
