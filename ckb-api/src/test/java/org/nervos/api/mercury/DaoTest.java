package org.nervos.api.mercury;

import com.google.gson.Gson;
import constant.AddressWithKeyHolder;
import constant.ApiFactory;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.nervos.ckb.type.transaction.Transaction;
import org.nervos.ckb.utils.AmountUtils;
import org.nervos.mercury.model.DepositPayloadBuilder;
import org.nervos.mercury.model.WithdrawPayloadBuilder;
import org.nervos.mercury.model.req.From;
import org.nervos.mercury.model.req.Source;
import org.nervos.mercury.model.req.item.Item;
import org.nervos.mercury.model.resp.TransactionCompletionResponse;
import utils.SignUtils;

public class DaoTest {

  @Test
  public void testDepositWithAddress() {
    DepositPayloadBuilder builder = new DepositPayloadBuilder();
    builder.from(
        From.newFrom(
            Arrays.asList(Item.newAddressItem(AddressWithKeyHolder.testAddress3())), Source.Free));
    builder.amount(AmountUtils.ckbToShannon(300));

    System.out.println(new Gson().toJson(builder));

    TransactionCompletionResponse transactionCompletionResponse = null;
    try {
      transactionCompletionResponse = ApiFactory.getApi().buildDepositTransaction(builder.build());
      System.out.println(new Gson().toJson(transactionCompletionResponse));

      Transaction signTx = SignUtils.sign(transactionCompletionResponse);

      String txHash = ApiFactory.getApi().sendTransaction(signTx);

      System.out.println(txHash);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testDepositWithIdentity() {
    DepositPayloadBuilder builder = new DepositPayloadBuilder();
    builder.from(
        From.newFrom(
            Arrays.asList(Item.newIdentityItemByCkb(AddressWithKeyHolder.testPubKey3())),
            Source.Free));
    builder.amount(AmountUtils.ckbToShannon(300));

    System.out.println(new Gson().toJson(builder));

    TransactionCompletionResponse transactionCompletionResponse = null;
    try {
      transactionCompletionResponse = ApiFactory.getApi().buildDepositTransaction(builder.build());
      Transaction signTx = SignUtils.sign(transactionCompletionResponse);

      String txHash = ApiFactory.getApi().sendTransaction(signTx);

      System.out.println(txHash);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testWithdraw() {
    WithdrawPayloadBuilder builder = new WithdrawPayloadBuilder();
    builder.from(Item.newAddressItem(AddressWithKeyHolder.testAddress3()));
    builder.payFee(AddressWithKeyHolder.testAddress1());

    System.out.println(new Gson().toJson(builder));

    TransactionCompletionResponse transactionCompletionResponse = null;
    try {
      transactionCompletionResponse = ApiFactory.getApi().buildWithdrawTransaction(builder.build());

      Transaction signTx = SignUtils.sign(transactionCompletionResponse);
      String txHash = ApiFactory.getApi().sendTransaction(signTx);

      System.out.println(txHash);
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(new Gson().toJson(transactionCompletionResponse));
  }
}
