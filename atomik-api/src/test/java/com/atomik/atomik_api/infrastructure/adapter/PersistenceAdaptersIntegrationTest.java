package com.atomik.atomik_api.infrastructure.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.model.SyncStatusType;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.infrastructure.persistence.AccountEntity;
import com.atomik.atomik_api.infrastructure.persistence.AccountMapper;
import com.atomik.atomik_api.infrastructure.persistence.CategoryEntity;
import com.atomik.atomik_api.infrastructure.persistence.CategoryMapper;
import com.atomik.atomik_api.infrastructure.persistence.JpaAccountRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaCategoryRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaUserRepository;
import com.atomik.atomik_api.infrastructure.persistence.TransactionMapper;
import com.atomik.atomik_api.infrastructure.persistence.UserEntity;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=month,year;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({
        AccountRepositoryAdapter.class,
        DatabaseCategoryRepositoryAdapter.class,
        DatabaseTransactionRepositoryAdapter.class,
        AccountMapper.class,
        CategoryMapper.class,
        TransactionMapper.class
})
class PersistenceAdaptersIntegrationTest {

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Autowired
    private DatabaseCategoryRepositoryAdapter categoryRepositoryAdapter;

    @Autowired
    private DatabaseTransactionRepositoryAdapter transactionRepositoryAdapter;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Test
    @DisplayName("Category adapter should preserve user relation and isDefault")
    void categoryAdapterShouldPreserveUserRelationAndIsDefault() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(userId, "John", "john@test.com", "hash", "BRL", LocalDateTime.now());
        jpaUserRepository.save(user);

        Category category = Category.createNewCategory(userId, "Food", "fork", "red", true);

        Category savedCategory = categoryRepositoryAdapter.save(category);
        Category loadedCategory = categoryRepositoryAdapter.findById(savedCategory.getId()).orElseThrow();

        assertEquals(userId, loadedCategory.getUserId());
        assertTrue(loadedCategory.getIsDefault());

        Category updatedCategory = new Category(loadedCategory.getId(), loadedCategory.getUserId(), "Groceries", "cart",
                "green", false);
        Category persistedUpdate = categoryRepositoryAdapter.update(updatedCategory).orElseThrow();
        assertEquals("Groceries", persistedUpdate.getName());
        assertEquals(false, persistedUpdate.getIsDefault());
    }

    @Test
    @DisplayName("Account adapter should persist balance updates")
    void accountAdapterShouldPersistBalanceUpdates() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(userId, "John", "john2@test.com", "hash", "BRL", LocalDateTime.now());
        jpaUserRepository.save(user);

        Account account = Account.createNewAccount(userId, "Wallet", AccountType.CHECKING, "BRL");
        Account savedAccount = accountRepositoryAdapter.save(account);
        Account updatedAccount = savedAccount.deposit(new BigDecimal("42.50"));

        Account persisted = accountRepositoryAdapter.update(updatedAccount).orElseThrow();

        assertEquals(new BigDecimal("42.50"), persisted.getBalance());
        assertTrue(accountRepositoryAdapter.existsByNameAndUserId("Wallet", userId));
    }

    @Test
    @DisplayName("Transaction adapter should persist and update foreign key relations")
    void transactionAdapterShouldPersistAndUpdateForeignKeyRelations() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(userId, "Jane", "jane@test.com", "hash", "BRL", LocalDateTime.now());
        jpaUserRepository.save(user);

        CategoryEntity category1 = new CategoryEntity(UUID.randomUUID(), user, "Food", "fork", "red", false);
        CategoryEntity category2 = new CategoryEntity(UUID.randomUUID(), user, "Salary", "money", "green", false);
        jpaCategoryRepository.save(category1);
        jpaCategoryRepository.save(category2);

        AccountEntity account1 = new AccountEntity(UUID.randomUUID(), user, "Checking", AccountType.CHECKING, "BRL",
                LocalDateTime.now(), new BigDecimal("100.00"));
        AccountEntity account2 = new AccountEntity(UUID.randomUUID(), user, "Savings", AccountType.SAVINGS, "BRL",
                LocalDateTime.now(), new BigDecimal("50.00"));
        jpaAccountRepository.save(account1);
        jpaAccountRepository.save(account2);

        Transaction transaction = new Transaction(UUID.randomUUID(), userId, category1.getId(), account1.getId(), null,
                new BigDecimal("10.00"), "Lunch", LocalDateTime.now(), TransactionType.EXPENSE,
                SyncStatusType.PENDING, LocalDateTime.now());

        transactionRepositoryAdapter.save(transaction);

        Transaction loaded = transactionRepositoryAdapter.findById(transaction.getId()).orElseThrow();
        assertEquals(category1.getId(), loaded.getCategoryId());
        assertEquals(account1.getId(), loaded.getSourceAccountId());

        Transaction updated = new Transaction(loaded.getId(), loaded.getUserId(), category2.getId(), account2.getId(),
                null, new BigDecimal("20.00"), "Salary", LocalDateTime.now(), TransactionType.REVENUE,
                loaded.getSyncStatus(), loaded.getCreatedAt());

        Transaction persisted = transactionRepositoryAdapter.update(updated).orElseThrow();

        assertEquals(category2.getId(), persisted.getCategoryId());
        assertEquals(account2.getId(), persisted.getSourceAccountId());
        assertEquals(new BigDecimal("20.00"), persisted.getAmount());
        assertNotNull(transactionRepositoryAdapter.findById(transaction.getId()).orElseThrow().getSourceAccountId());
    }
}
