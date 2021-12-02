package crushers.controllers;

import crushers.App;
import crushers.model.Bank;
import crushers.model.Customer;
import crushers.model.PaymentAccount;
import crushers.model.Transaction;
import crushers.util.Http;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AccountTransferController implements Initializable {
    private Customer currentCustomer = App.currentCustomer;

    private Stage stage;
    private Parent root;



    @FXML
    private Label transferFundsLabel, fromLabel, toLabel, amountLabel, sekLabel, commentLabel, errorLabel;

    @FXML
    private ChoiceBox<PaymentAccount> accountFromBox, accountToBox;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea commentArea;

    @FXML
    private Button transferFunds, done;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<PaymentAccount> userAccounts = App.currentCustomer.getAccountList();
        accountFromBox.setStyle("-fx-font-family: SansSerif");
        accountToBox.setStyle("-fx-font-family: SansSerif");
        accountFromBox.getItems().addAll(userAccounts);
        accountToBox.getItems().addAll(userAccounts);
        accountFromBox.setOnAction(this::firstChoice);
    }

    private void firstChoice(Event event) {
        accountToBox.getItems().remove(accountFromBox.getValue());
    }

    @FXML
    private void transferFunds(ActionEvent e) throws IOException {
        if (accountFromBox.getValue() == null) {
            errorLabel.setText("Please select an account to transfer funds from.");
        } else if (accountToBox.getValue() == null) {
            errorLabel.setText("Please select an account to transfer funds to.");
        } else if (amountField.getText() == null) {
            errorLabel.setText("Please enter an amount to transfer!");
        } else if (!amountField.getText().matches("^[0-9]+$")){
            errorLabel.setText("Please enter a valid numeric amount.");
        } else {
           PaymentAccount paymentAccountFrom = accountFromBox.getValue();
           PaymentAccount paymentAccountTo =  accountToBox.getValue();
           double amountSek = Double.parseDouble(amountField.getText());
           String comment = commentArea.getText();
           if (Double.parseDouble(amountField.getText()) > paymentAccountFrom.getBalance()) {
               errorLabel.setText("Amount to transfer can not be greater than the account balance. Please enter a smaller amount.");
           } else {
               Transaction transaction = new Transaction(paymentAccountFrom, paymentAccountTo, amountSek, comment,null);
               paymentAccountFrom.withdraw(amountSek);
               paymentAccountTo.deposit(amountSek);
               paymentAccountFrom.addTransactionToMap(transaction);
               paymentAccountTo.addTransactionToMap(transaction);
               errorLabel.setStyle("-fx-text-fill: green");
               errorLabel.setText("Transfer successful!");
               // Http.post("/transactions", transaction);
            }
        }
    }
    @FXML
    public void done(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("crushers/views/AccountView.fxml"));
        root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Account Overview");
        stage.show();
    }
}



