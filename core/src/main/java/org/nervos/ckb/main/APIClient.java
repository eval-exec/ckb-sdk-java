package org.nervos.ckb.main;

import com.google.gson.Gson;
import org.nervos.ckb.response.item.Block;
import org.nervos.ckb.service.HttpService;
import org.nervos.ckb.service.CKBService;

public class APIClient {

    private static final String NODE_URL = "http://localhost:8114/";

    private static CKBService ckbService;


    static {
        HttpService.setDebug(true);
        ckbService = CKBService.build(new HttpService(NODE_URL));
    }

    public static void main(String[] args) {
        try {

            String blockHash = ckbService.getBlockHash(1).send().getBlockHash();
            System.out.println("Second block hash is " + blockHash);

            Block block = ckbService.getBlock(blockHash).send().getBlock();
            System.out.println("Second block is " + new Gson().toJson(block));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
