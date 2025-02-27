package com.w.wallet_lib

import walletapi.*

object OBWallet {

    //1:chinese 2:english
    fun createMnem(mnemLangType: Int): String {
        return when (mnemLangType) {
            1 -> Walletapi.newMnemonicString(1, 160)
            2 -> Walletapi.newMnemonicString(0, 128)
            else -> Walletapi.newMnemonicString(1, 160)
        }
    }

    fun createWallet(mnem: String, chain: String): Wallet {
        val hdWallet = Walletapi.newWalletFromMnemonic_v2(chain, mnem)
        return Wallet(
            hdWallet.newAddress_v2(0),
            Walletapi.byteTohex(hdWallet.newKeyPriv(0)),
            Walletapi.byteTohex(hdWallet.newKeyPub(0))
        )
    }

    fun privToAddr(chain: String, priv: String): String {
        val pub = Walletapi.privkeyToPub_v2(chain, Walletapi.hexTobyte(priv))
        return Walletapi.pubToAddress_v2(chain, pub)
    }

    fun pubToAddr(chain: String, pub: String): String {
        return Walletapi.pubToAddress_v2(chain, Walletapi.hexTobyte(pub))
    }

    fun getBalance(addr: String, chain: String, tokenSymbol: String, goNoderUrl: String): String {
        val walletBalance = WalletBalance()
        walletBalance.address = addr
        walletBalance.cointype = chain
        walletBalance.tokenSymbol = tokenSymbol
        walletBalance.util = getUtil(goNoderUrl)
        return Walletapi.byteTostring(Walletapi.getbalance(walletBalance))
    }

    fun getTranList(
        addr: String,
        chain: String,
        tokenSymbol: String,
        type: Long,
        page: Long,
        count: Long,
        goNoderUrl: String
    ): String {

        val walletQueryByAddr = WalletQueryByAddr()
        val queryByPage = QueryByPage()
        queryByPage.cointype = chain
        queryByPage.tokenSymbol = if (chain == tokenSymbol) "" else tokenSymbol
        queryByPage.address = addr
        queryByPage.count = count
        queryByPage.direction = 0
        queryByPage.index = page * count
        queryByPage.type = type
        walletQueryByAddr.queryByPage = queryByPage
        walletQueryByAddr.util = getUtil(goNoderUrl)
        return Walletapi.byteTostring(Walletapi.queryTransactionsByaddress(walletQueryByAddr))
    }

    fun getTranByTxid(
        txid: String,
        chain: String,
        tokenSymbol: String,
        goNoderUrl: String
    ): String {
        val walletQueryByTxid = WalletQueryByTxid()
        walletQueryByTxid.cointype = chain
        walletQueryByTxid.tokenSymbol = tokenSymbol
        walletQueryByTxid.txid = txid
        walletQueryByTxid.util = getUtil(goNoderUrl)
        return Walletapi.byteTostring(Walletapi.queryTransactionByTxid(walletQueryByTxid))
    }

    fun createTran(
        chain: String,
        fromAddr: String,
        toAddr: String,
        amount: Double,
        fee: Double,
        note: String,
        tokenSymbol: String,
        goNoderUrl: String
    ): String {
        val walletTx = WalletTx()
        walletTx.cointype = chain
        walletTx.tokenSymbol = tokenSymbol
        //Txdata
        val txdata = Txdata()
        txdata.amount = amount
        txdata.fee = fee
        txdata.from = fromAddr
        txdata.to = toAddr
        txdata.note = note
        walletTx.tx = txdata
        walletTx.util = getUtil(goNoderUrl)
        return Walletapi.byteTostring(Walletapi.createRawTransaction(walletTx))
    }

    fun signTran(chain: String, unSignData: String, priv: String): String {
        val signData = SignData()
        signData.cointype = chain
        signData.data = Walletapi.stringTobyte(unSignData)
        signData.privKey = priv
        signData.addressID
        return Walletapi.signRawTransaction(signData);
    }

    fun signTran(chain: String, unSignData: String, priv: String, addressID: Int): String {
        val signData = SignData()
        signData.cointype = chain
        signData.data = Walletapi.stringTobyte(unSignData)
        signData.privKey = priv
        signData.addressID = addressID
        return Walletapi.signRawTransaction(signData);
    }

    fun sendTran(chain: String, tokenSymbol: String, signData: String, goNoderUrl: String): String {
        val sendTx = WalletSendTx()
        sendTx.cointype = chain
        sendTx.signedTx = signData
        sendTx.tokenSymbol = tokenSymbol
        sendTx.util = getUtil(goNoderUrl)
        return Walletapi.byteTostring(Walletapi.sendRawTransaction(sendTx))
    }

    fun pcTran(
        to: String,
        tokenSymbol: String,
        execer: String,
        txpriv: String,
        amount: Double,
        note: String,
        feePriv: String,
        coinsForFee: Boolean,
        tokenfee: Double,
        tokenfeeAddr: String,
        fee: Double
    ): GsendTxResp {
        val gsendTx = GsendTx()
        gsendTx.to = to
        gsendTx.tokenSymbol = tokenSymbol
        gsendTx.execer = execer
        gsendTx.txpriv = txpriv
        gsendTx.amount = amount
        gsendTx.note = note
        gsendTx.feepriv = feePriv
        gsendTx.coinsForFee = coinsForFee
        gsendTx.tokenFee = tokenfee
        gsendTx.tokenFeeAddr = tokenfeeAddr
        gsendTx.fee = fee
        val gsendTxResp = Walletapi.coinsTxGroup(gsendTx)
        return gsendTxResp
    }

    fun byteTohex(byteArray: ByteArray): String {
        return Walletapi.byteTohex(byteArray)
    }

    private fun getUtil(goNoderUrl: String): Util {
        val util = Util()
        util.node = goNoderUrl
        return util
    }
}