package io.github.leonardofrs.funds_service.application.usecases.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.leonardofrs.funds_service.domain.gateway.transactions.CountTransactionsGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.RetrieveTransactionsGateway;
import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.vo.Page;
import io.github.leonardofrs.funds_service.domain.vo.PageResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultRetrieveTransactionsTest {

  @Mock
  private RetrieveTransactionsGateway retrieveTransactionsGateway;

  @Mock
  private CountTransactionsGateway countTransactionsGateway;

  private DefaultRetrieveTransactions useCase;

  @BeforeEach
  void setUp() {
    useCase = new DefaultRetrieveTransactions(retrieveTransactionsGateway, countTransactionsGateway);
  }

  @Test
  @DisplayName("should_returnEmptyPage_whenNoTransactionsFound")
  void should_returnEmptyPage_whenNoTransactionsFound() {
    UUID clientId = UUID.randomUUID();
    Page page = new Page(0, 10);

    when(countTransactionsGateway.execute(clientId)).thenReturn(0L);

    PageResult<Transaction> result = useCase.execute(clientId, page);

    assertThat(result.items()).isEmpty();
    assertThat(result.total()).isZero();

    verify(retrieveTransactionsGateway, never()).execute(clientId, page);
  }

  @Test
  @DisplayName("should_returnTransactions_whenTransactionsExist")
  void should_returnTransactions_whenTransactionsExist() {
    UUID clientId = UUID.randomUUID();
    Page page = new Page(0, 10);

    Transaction tx1 = mock(Transaction.class);
    Transaction tx2 = mock(Transaction.class);
    List<Transaction> transactionList = List.of(tx1, tx2);

    when(countTransactionsGateway.execute(clientId)).thenReturn(2L);
    when(retrieveTransactionsGateway.execute(clientId, page)).thenReturn(transactionList);

    PageResult<Transaction> result = useCase.execute(clientId, page);

    assertThat(result.items()).containsExactlyElementsOf(transactionList);
    assertThat(result.total()).isEqualTo(2);

    verify(retrieveTransactionsGateway).execute(clientId, page);
  }

  @Test
  @DisplayName("should_throwException_whenClientIdIsNull")
  void should_throwException_whenClientIdIsNull() {
    Page page = new Page(0, 10);

    assertThatThrownBy(() -> useCase.execute(null, page))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("should_throwException_whenPageIsNull")
  void should_throwException_whenPageIsNull() {
    UUID clientId = UUID.randomUUID();

    assertThatThrownBy(() -> useCase.execute(clientId, null))
        .isInstanceOf(NullPointerException.class);
  }
}