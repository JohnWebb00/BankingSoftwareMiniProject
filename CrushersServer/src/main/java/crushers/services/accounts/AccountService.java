package crushers.services.accounts;

import java.util.ArrayList;
import java.util.Collection;

import crushers.models.Bank;
import crushers.models.accounts.Account;
import crushers.models.exchangeInformation.Transaction;
import crushers.models.users.Clerk;
import crushers.models.users.Customer;
import crushers.models.users.User;
import crushers.server.httpExceptions.*;
import crushers.services.banks.BankService;
import crushers.services.customers.CustomerService;

public class AccountService {
  
  private final JsonAccountStorage storage;
  private final CustomerService customerService;
  private final BankService bankService;

  public AccountService(CustomerService customerService, BankService bankService, JsonAccountStorage storage) {
    this.customerService = customerService;
    this.bankService = bankService;
    this.storage = storage;
  }

  /**
   *
   * @param creator is the logged-in User
   * @param account is the account passed by the creator for creation
   * creates an account as specified by the User, allowing for creation by Staff and Customers alike
   *
   * @example {
   *     id: int,
   *     bank: {
   *         id: int,
   *         name: String,
   *         logo: String,
   *     },
   *     owner: {
   *         id: int,
   *         email: String,
   *         firstName: String,
   *         lastName: String,
   *         address: String,
   *     },
   *     type: "savings" | "payment",
   *     number: String,
   *     balance: double,
   * }
   *
   * @return the account created
   * @throws Exception if the account details are invalid
   */
  public Account create(User creator, Account account) throws Exception {
    if (creator instanceof Clerk) {
      Clerk clerk = (Clerk) creator;
      account.setBank(bankService.get(clerk.getWorksAt().getId())); // set the bank the account is opened at
      account.setOwner(customerService.get(account.getOwner().getId())); // check if the customer exists
    }
    else if (creator instanceof Customer) {
      Customer customer = (Customer) creator;
      account.setBank(bankService.get(account.getBank().getId())); // check if the bank exists
      account.setOwner(customerService.get(customer.getId())); // set the owner of the account
    }

    account.setNumber(account.getBank().generateAccountNumber());
    return storage.create(account);
  }

  public Account get(User loggedInUser, int id) throws Exception {
    Account account = storage.get(id);
    if (account == null) throw new ForbiddenException();

    boolean isBankStaff = (loggedInUser instanceof Clerk && account.getBank().equals(((Clerk)loggedInUser).getWorksAt()));

    if (!account.getOwner().equals(loggedInUser) && !isBankStaff) {
      throw new ForbiddenException();
    }

    return account;
  }

  public boolean exists(int id) throws Exception{
    return (storage.get(id) != null);
  }

  public void commit(Transaction transaction) throws Exception {
    if (transaction == null) throw new ForbiddenException();

    if(transaction.getFrom() != (null)){
      Account sender = storage.get(transaction.getFrom().getId());

      if(transaction.getTo() != null){
        Account receiver = storage.get(transaction.getTo().getId());
        receiver.setBalance(receiver.getBalance() + transaction.getAmount());

        if (sender.getBalance() <= transaction.getAmount()) {
          throw new BadRequestException(
                  "Cannot create Transaction: Account with id: " + receiver.getId() + " does not have enough funds to create this Transaction"
          );
        }
        sender.setBalance(sender.getBalance() - transaction.getAmount());
      }
      else{
        sender.setBalance(sender.getBalance() - transaction.getAmount());
      }
    }
    else{
      Account receiver = storage.get(transaction.getTo().getId());
      receiver.setBalance(receiver.getBalance() + transaction.getAmount());
    }
  }

  public Collection<Account> getOfCustomer(Customer customer) throws Exception {
    Collection<Account> accounts = storage.getAccountsOfCustomer(customer);
    if (accounts == null) accounts = new ArrayList<>();
    return accounts;
  }

  public Collection<Account> getOfBank(Bank bank) throws Exception {
    Collection<Account> accounts = storage.getAccountsOfBank(bank);
    if (accounts == null) accounts = new ArrayList<>();
    return accounts;
  }

  public Collection<Customer> getCustomersAtBank(Bank bank) {
    return storage.getCustomersAtBank(bank);
  }

  public Account getAccountWithId(Customer loggedIn, int id) throws Exception{
    for(Account account : storage.getAll()){
      if(account.getId() == id){
        return account;
      }
      else{
        throw new BadRequestException("Account with id " + id + " does not exist for this bank");
      }
    }
    return null;
  }
}
