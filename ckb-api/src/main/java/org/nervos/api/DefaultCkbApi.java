package org.nervos.api;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.nervos.ckb.CkbRpcApi;
import org.nervos.ckb.service.Api;
import org.nervos.ckb.service.RpcResponse;
import org.nervos.ckb.service.RpcService;
import org.nervos.ckb.type.BannedAddress;
import org.nervos.ckb.type.BannedResultAddress;
import org.nervos.ckb.type.Block;
import org.nervos.ckb.type.BlockEconomicState;
import org.nervos.ckb.type.BlockchainInfo;
import org.nervos.ckb.type.Consensus;
import org.nervos.ckb.type.Cycles;
import org.nervos.ckb.type.Epoch;
import org.nervos.ckb.type.Header;
import org.nervos.ckb.type.NodeInfo;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.PeerNodeInfo;
import org.nervos.ckb.type.RawTxPool;
import org.nervos.ckb.type.RawTxPoolVerbose;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.type.SyncState;
import org.nervos.ckb.type.TransactionProof;
import org.nervos.ckb.type.TxPoolInfo;
import org.nervos.ckb.type.cell.CellWithStatus;
import org.nervos.ckb.type.param.OutputsValidator;
import org.nervos.ckb.type.transaction.Transaction;
import org.nervos.ckb.type.transaction.TransactionWithStatus;
import org.nervos.indexer.CkbIndexerApi;
import org.nervos.indexer.DefaultIndexerApi;
import org.nervos.indexer.model.SearchKey;
import org.nervos.indexer.model.resp.CellCapacityResponse;
import org.nervos.indexer.model.resp.CellsResponse;
import org.nervos.indexer.model.resp.TipResponse;
import org.nervos.indexer.model.resp.TransactionResponse;
import org.nervos.mercury.DefaultMercuryApi;
import org.nervos.mercury.MercuryApi;
import org.nervos.mercury.model.req.CollectAssetPayload;
import org.nervos.mercury.model.req.CreateAssetAccountPayload;
import org.nervos.mercury.model.req.GetBalancePayload;
import org.nervos.mercury.model.req.GetGenericBlockPayload;
import org.nervos.mercury.model.req.QueryGenericTransactionsPayload;
import org.nervos.mercury.model.req.TransferPayload;
import org.nervos.mercury.model.resp.GenericBlockResponse;
import org.nervos.mercury.model.resp.GenericTransactionWithStatusResponse;
import org.nervos.mercury.model.resp.GetBalanceResponse;
import org.nervos.mercury.model.resp.QueryGenericTransactionsResponse;
import org.nervos.mercury.model.resp.TransactionCompletionResponse;

/** Copyright © 2019 Nervos Foundation. All rights reserved. */
public class DefaultCkbApi implements CkbApi {
  private CkbRpcApi ckbApi;

  private MercuryApi mercuryApi;

  private CkbIndexerApi ckbIndexerApi;

  public DefaultCkbApi(String mercuryUrl, boolean isDebug) {
    RpcService rpcService = new RpcService(mercuryUrl, isDebug);
    this.ckbApi = new Api(rpcService);
    this.ckbIndexerApi = new DefaultIndexerApi(rpcService);
    this.mercuryApi = new DefaultMercuryApi(rpcService);
  }

  @Override
  public TipResponse getTip() throws IOException {
    return this.ckbIndexerApi.getTip();
  }

  @Override
  public CellsResponse getCells(SearchKey searchKey, String order, String limit, String afterCursor)
      throws IOException {
    return this.ckbIndexerApi.getCells(searchKey, order, limit, afterCursor);
  }

  @Override
  public TransactionResponse getTransactions(
      SearchKey searchKey, String order, String limit, String afterCursor) throws IOException {
    return this.ckbIndexerApi.getTransactions(searchKey, order, limit, afterCursor);
  }

  @Override
  public CellCapacityResponse getCellsCapacity(SearchKey searchKey) throws IOException {
    return this.ckbIndexerApi.getCellsCapacity(searchKey);
  }

  @Override
  public Block getBlock(String blockHash) throws IOException {
    return this.ckbApi.getBlock(blockHash);
  }

  @Override
  public Block getBlockByNumber(String blockNumber) throws IOException {
    return this.ckbApi.getBlockByNumber(blockNumber);
  }

  @Override
  public TransactionWithStatus getTransaction(String transactionHash) throws IOException {
    return this.ckbApi.getTransaction(transactionHash);
  }

  @Override
  public String getBlockHash(String blockNumber) throws IOException {
    return this.ckbApi.getBlockHash(blockNumber);
  }

  @Override
  public BlockEconomicState getBlockEconomicState(String blockHash) throws IOException {
    return this.ckbApi.getBlockEconomicState(blockHash);
  }

  @Override
  public Header getTipHeader() throws IOException {
    return this.ckbApi.getTipHeader();
  }

  @Override
  public CellWithStatus getLiveCell(OutPoint outPoint, boolean withData) throws IOException {
    return this.ckbApi.getLiveCell(outPoint, withData);
  }

  @Override
  public BigInteger getTipBlockNumber() throws IOException {
    return this.ckbApi.getTipBlockNumber();
  }

  @Override
  public Epoch getCurrentEpoch() throws IOException {
    return this.ckbApi.getCurrentEpoch();
  }

  @Override
  public Epoch getEpochByNumber(String epochNumber) throws IOException {
    return this.ckbApi.getEpochByNumber(epochNumber);
  }

  @Override
  public Header getHeader(String blockHash) throws IOException {
    return this.ckbApi.getHeader(blockHash);
  }

  @Override
  public Header getHeaderByNumber(String blockNumber) throws IOException {
    return this.ckbApi.getHeaderByNumber(blockNumber);
  }

  @Override
  public TransactionProof getTransactionProof(List<String> txHashes) throws IOException {
    return this.ckbApi.getTransactionProof(txHashes);
  }

  @Override
  public TransactionProof getTransactionProof(List<String> txHashes, String blockHash)
      throws IOException {
    return this.ckbApi.getTransactionProof(txHashes, blockHash);
  }

  @Override
  public List<String> verifyTransactionProof(TransactionProof transactionProof) throws IOException {
    return this.ckbApi.verifyTransactionProof(transactionProof);
  }

  @Override
  public Consensus getConsensus() throws IOException {
    return this.ckbApi.getConsensus();
  }

  @Override
  public String getBlockMedianTime(String blockHash) throws IOException {
    return this.ckbApi.getBlockMedianTime(blockHash);
  }

  @Override
  public BlockchainInfo getBlockchainInfo() throws IOException {
    return this.ckbApi.getBlockchainInfo();
  }

  @Override
  public TxPoolInfo txPoolInfo() throws IOException {
    return this.ckbApi.txPoolInfo();
  }

  @Override
  public String clearTxPool() throws IOException {
    return this.ckbApi.clearTxPool();
  }

  @Override
  public RawTxPool getRawTxPool() throws IOException {
    return this.ckbApi.getRawTxPool();
  }

  @Override
  public RawTxPoolVerbose getRawTxPoolVerbose() throws IOException {
    return this.ckbApi.getRawTxPoolVerbose();
  }

  @Override
  public String sendTransaction(Transaction transaction) throws IOException {
    return this.ckbApi.sendTransaction(transaction);
  }

  @Override
  public String sendTransaction(Transaction transaction, OutputsValidator outputsValidator)
      throws IOException {
    return this.ckbApi.sendTransaction(transaction, outputsValidator);
  }

  @Override
  public NodeInfo localNodeInfo() throws IOException {
    return this.ckbApi.localNodeInfo();
  }

  @Override
  public List<PeerNodeInfo> getPeers() throws IOException {
    return this.ckbApi.getPeers();
  }

  @Override
  public SyncState syncState() throws IOException {
    return this.ckbApi.syncState();
  }

  @Override
  public String setNetworkActive(Boolean state) throws IOException {
    return this.ckbApi.setNetworkActive(state);
  }

  @Override
  public String addNode(String peerId, String address) throws IOException {
    return this.ckbApi.addNode(peerId, address);
  }

  @Override
  public String removeNode(String peerId) throws IOException {
    return this.ckbApi.removeNode(peerId);
  }

  @Override
  public String setBan(BannedAddress bannedAddress) throws IOException {
    return this.ckbApi.setBan(bannedAddress);
  }

  @Override
  public List<BannedResultAddress> getBannedAddresses() throws IOException {
    return this.ckbApi.getBannedAddresses();
  }

  @Override
  public String clearBannedAddresses() throws IOException {
    return this.ckbApi.clearBannedAddresses();
  }

  @Override
  public String pingPeers() throws IOException {
    return this.ckbApi.pingPeers();
  }

  @Override
  public Cycles dryRunTransaction(Transaction transaction) throws IOException {
    return this.ckbApi.dryRunTransaction(transaction);
  }

  @Override
  @Deprecated
  public String computeTransactionHash(Transaction transaction) throws IOException {
    return this.ckbApi.computeTransactionHash(transaction);
  }

  @Override
  @Deprecated
  public String computeScriptHash(Script script) throws IOException {
    return this.ckbApi.computeScriptHash(script);
  }

  @Override
  public String calculateDaoMaximumWithdraw(OutPoint outPoint, String withdrawBlockHash)
      throws IOException {
    return this.ckbApi.calculateDaoMaximumWithdraw(outPoint, withdrawBlockHash);
  }

  @Override
  public List<RpcResponse> batchRPC(List<List> requests) throws IOException {
    return this.ckbApi.batchRPC(requests);
  }

  @Override
  public GetBalanceResponse getBalance(GetBalancePayload payload) throws IOException {
    return this.mercuryApi.getBalance(payload);
  }

  @Override
  public TransactionCompletionResponse buildTransferTransaction(TransferPayload payload)
      throws IOException {
    return this.mercuryApi.buildTransferTransaction(payload);
  }

  @Override
  public TransactionCompletionResponse buildAssetAccountCreationTransaction(
      CreateAssetAccountPayload payload) throws IOException {
    return this.mercuryApi.buildAssetAccountCreationTransaction(payload);
  }

  @Override
  public GenericTransactionWithStatusResponse getGenericTransaction(String txHash)
      throws IOException {
    return this.mercuryApi.getGenericTransaction(txHash);
  }

  @Override
  public GenericBlockResponse getGenericBlock(GetGenericBlockPayload payload) throws IOException {
    return this.mercuryApi.getGenericBlock(payload);
  }

  @Override
  public List<String> registerAddresses(List<String> normalAddresses) throws IOException {
    return this.mercuryApi.registerAddresses(normalAddresses);
  }

  @Override
  public TransactionCompletionResponse buildAssetCollectionTransaction(CollectAssetPayload payload)
      throws IOException {
    return this.mercuryApi.buildAssetCollectionTransaction(payload);
  }

  @Override
  public QueryGenericTransactionsResponse queryGenericTransactions(
      QueryGenericTransactionsPayload payload) throws IOException {
    return this.mercuryApi.queryGenericTransactions(payload);
  }
}