package transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.nervos.ckb.Network;
import org.nervos.ckb.sign.TransactionWithScriptGroups;
import org.nervos.ckb.transaction.CkbTransactionBuilder;
import org.nervos.ckb.transaction.TransactionBuilderConfiguration;
import org.nervos.ckb.transaction.handler.TypeIdHandler;
import org.nervos.ckb.type.*;
import org.nervos.ckb.utils.Numeric;
import org.nervos.ckb.utils.Utils;
import org.nervos.ckb.utils.address.Address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class CkbTransactionBuilderTest {
  byte[] fakeHash = Numeric.hexStringToByteArray("0x0000000000000000000000000000000000000000000000000000000000000000");
  Address sender = Address.decode("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq02cgdvd5mng9924xarf3rflqzafzmzlpsuhh83c");
  Script lock = sender.getScript();

  @Test
  void testSingleInput() {
    Iterator<TransactionInput> iterator = newTransactionInputs();
    TransactionBuilderConfiguration configuration = new TransactionBuilderConfiguration(Network.TESTNET);
    configuration.setFeeRate(1000);
    TransactionWithScriptGroups txWithGroups = new CkbTransactionBuilder(configuration, iterator)
        .addOutput("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq2qf8keemy2p5uu0g0gn8cd4ju23s5269qk8rg4r",
                   50100000000L)
        .setChangeOutput(sender.encode())
        .build();

    Transaction tx = txWithGroups.getTxView();
    Assertions.assertEquals(1, tx.inputs.size());
    Assertions.assertEquals(1, txWithGroups.scriptGroups.size());
    Assertions.assertEquals(lock, txWithGroups.scriptGroups.get(0).getScript());
    Assertions.assertEquals(2, tx.outputs.size());
    long fee = 100000000000L - tx.outputs.get(0).capacity - tx.outputs.get(1).capacity;
    Assertions.assertEquals(464, fee);
  }

  @Test
  void testTypeId() {
    Iterator<TransactionInput> iterator = newTransactionInputs();
    TransactionBuilderConfiguration configuration = new TransactionBuilderConfiguration(Network.TESTNET);
    configuration.setFeeRate(1000);
    CellOutput output = new CellOutput(120, Address.decode("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq2qf8keemy2p5uu0g0gn8cd4ju23s5269qk8rg4r").getScript(), new Script(TypeIdHandler.TYPE_ID_CODE_HASH, null));
    TransactionWithScriptGroups txWithGroups = new CkbTransactionBuilder(configuration, iterator)
        .addOutput(output, new byte[64])
        .setChangeOutput(sender.encode())
        .build();

    Transaction tx = txWithGroups.getTxView();

    Assertions.assertEquals(1, tx.inputs.size());
    Assertions.assertEquals(2, txWithGroups.scriptGroups.size());
    Assertions.assertTrue(txWithGroups.scriptGroups.stream().anyMatch(scriptGroup -> scriptGroup.getScript() == lock));
    Assertions.assertTrue(txWithGroups.scriptGroups.stream().anyMatch(g -> g.getGroupType() == ScriptType.TYPE && g.getScript().codeHash == TypeIdHandler.TYPE_ID_CODE_HASH && g.getScript().args.length == 32 && !Arrays.equals(g.getScript().args, TypeIdHandler.ZERO_ARGS)));
    Assertions.assertEquals(2, tx.outputs.size());
    long fee = 100000000000L - tx.outputs.get(0).capacity - tx.outputs.get(1).capacity;
    Assertions.assertEquals(613, fee);
  }

  @Test
  void testForceSmallFeeAsChange() {
    Iterator<TransactionInput> inputs = newTransactionInputs();
    TransactionBuilderConfiguration configuration = new TransactionBuilderConfiguration(Network.TESTNET);
    configuration.setForceSmallChangeAsFee(Utils.ckbToShannon(1));
    TransactionWithScriptGroups txWithGroups = new CkbTransactionBuilder(configuration, inputs)
        .addOutput("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq2qf8keemy2p5uu0g0gn8cd4ju23s5269qk8rg4r", Utils.ckbToShannon(1099))
        .setChangeOutput(sender.encode())
        .build();
    Assertions.assertEquals(txWithGroups.getTxView().outputs.size(), 1);
  }

  @Test
  void testForceSmallFeeAsChangeStillChange() {
    Iterator<TransactionInput> inputs = newTransactionInputs();
    TransactionBuilderConfiguration configuration = new TransactionBuilderConfiguration(Network.TESTNET);
    configuration.setForceSmallChangeAsFee(Utils.ckbToShannon(1));
    TransactionWithScriptGroups txWithGroups = new CkbTransactionBuilder(configuration, inputs)
        .addOutput("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq2qf8keemy2p5uu0g0gn8cd4ju23s5269qk8rg4r", Utils.ckbToShannon(1001))
        .setChangeOutput(sender.encode())
        .build();
    Transaction txView = txWithGroups.getTxView();
    Assertions.assertEquals(2, txView.inputs.size());
    Assertions.assertEquals(2, txView.outputs.size());
    Assertions.assertEquals(Utils.ckbToShannon(1001), txView.outputs.get(0).capacity);
    Assertions.assertEquals(9899999484L, txView.outputs.get(1).capacity);
  }

  @Test
  void testForceSmallFeeAsChangeFailure() {
    Iterator<TransactionInput> inputs = newTransactionInputs();
    TransactionBuilderConfiguration configuration = new TransactionBuilderConfiguration(Network.TESTNET);
    // The change will be not small enough for forceSmallChangeAsFee but not big enough for a change output.
    configuration.setForceSmallChangeAsFee(Utils.ckbToShannon(0.5));
    Exception exception = null;
    try {
      TransactionWithScriptGroups txWithGroups = new CkbTransactionBuilder(configuration, inputs)
          .addOutput("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq2qf8keemy2p5uu0g0gn8cd4ju23s5269qk8rg4r", Utils.ckbToShannon(1099))
          .setChangeOutput(sender.encode())
          .build();
    } catch (Exception e) {
      exception = e;
    }
    Assertions.assertNotNull(exception);
    Assertions.assertEquals("No enough capacity", exception.getMessage());
  }

  @Test
  void testMultipleInputs() {
    String sender = "ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq2qf8keemy2p5uu0g0gn8cd4ju23s5269qk8rg4r";

    TransactionBuilderConfiguration configuration = new TransactionBuilderConfiguration(Network.TESTNET);
    configuration.setFeeRate(1000);
    TransactionWithScriptGroups txWithGroups = new CkbTransactionBuilder(configuration, newTransactionInputs())
        .addOutput("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq2qf8keemy2p5uu0g0gn8cd4ju23s5269qk8rg4r",
                   100000000000L)
        .setChangeOutput(sender)
        .build();

    Transaction tx = txWithGroups.getTxView();
    Assertions.assertEquals(2, tx.inputs.size());
    Assertions.assertEquals(1, txWithGroups.scriptGroups.size());
    Assertions.assertEquals(lock, txWithGroups.scriptGroups.get(0).getScript());
    Assertions.assertEquals(2, tx.outputs.size());
    long fee = 110000000000L - tx.outputs.get(0).capacity - tx.outputs.get(1).capacity;
    Assertions.assertEquals(516, fee);
  }

  private Iterator<TransactionInput> newTransactionInputs() {
    List<TransactionInput> inputs = new ArrayList<>();

    CellInput cellInput = new CellInput(new OutPoint(fakeHash, 0), 0);
    CellOutput cellOutput = new CellOutput(100000000000L, lock);  // 1000 CKB
    inputs.add(new TransactionInput(cellInput, cellOutput));

    cellInput = new CellInput(new OutPoint(fakeHash, 0), 0);
    cellOutput = new CellOutput(10000000000L, lock); // 100 CKB
    inputs.add(new TransactionInput(cellInput, cellOutput));

    return inputs.iterator();
  }
}
