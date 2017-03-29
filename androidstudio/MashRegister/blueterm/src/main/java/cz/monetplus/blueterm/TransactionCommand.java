package cz.monetplus.blueterm;

import cz.monetplus.blueterm.worker.HandleOperations;

public enum TransactionCommand {
    /**
     * Navaze pouze spojeni s terminalem ktere drzi do volani disconnect, nic ovsem nevyvola.
     * Pouziva se pokud chcete pracovat s terminalem, jako by mel vlastni konentivitu
     */
    ONLY_CONNECT(HandleOperations.CallConnect),

    /**
     * MBCA handshake
     */
    MBCA_HANDSHAKE(HandleOperations.CallMbcaHandshake),

    /**
     * Uzaverka
     */
    MBCA_BALANCING(HandleOperations.CallMbcaBalancing),

    /**
     * TMS volani pro parametry
     */
    MBCA_PARAMETERS(HandleOperations.CallMbcaParameters),

    /**
     * Platba
     */
    MBCA_PAY(HandleOperations.CallMbcaPay),

    /**
     * Refund
     */
//    MBCA_REFUND(HandleOperations.CallMbcaRefund),

    /*
        Vrati posledni transakci
    */
    MBCA_LAST_TRAN(HandleOperations.CallMbcaGetLastTran),

    /**
     * Reverzuje posledni transakci (storno)
     */
    MBCA_REVERSAL(HandleOperations.CallMbcaReversal),

    /**
     * Vrati Applikacni informace o MBCA na terminalu.
     */
    MBCA_INFO(HandleOperations.CallMbcaInfo),

    /**
     * Informace o uzivateli, pokud je terminalem podporovan
     */
    MBCA_ACCOUNT_INFO(HandleOperations.CallMbcaAccountInfo),

    /**
     * MVTA handshake
     */
    MVTA_HANDSHAKE(HandleOperations.CallMvtaHandshake),

    /**
     * Info o aplikaci na terminalu
     */
    MVTA_INFO(HandleOperations.CallMvtaInfo),

    /**
     * Posledni MVTA transakce
     */
    MVTA_LAST_TRAN(HandleOperations.CallMvtaGetLastTran),

    /**
     * Volani na TMS z MVTA aplikace
     */
    MVTA_PARAMETERS(HandleOperations.CallMvtaParameters),

    /**
     * MVTA dobijeni
     */
    MVTA_RECHARGE(HandleOperations.CallMvtaRecharging),

    /**
     * Aktivace
     */
    // SMART_SHOP_ACTIVATE(HandleOperations.CallSmartShopActivate),

    /**
     * Deaktivace
     */
    //SMART_SHOP_DEACTIVATE(HandleOperations.CallSmartShopDeactivate),

    /**
     * Smartshop platba
     */
    SMART_SHOP_PAY(HandleOperations.CallSmartShopPay),

    /**
     * Smartshop navrat
     */
    SMART_SHOP_RETURN(HandleOperations.CallSmartShopReturn),

    /**
     * Dobijeni telefonnich cisel.
     */
    SMART_SHOP_RECHARGING(HandleOperations.CallSmartShopRecharging),

    /**
     * Aktualni zustatek karty.
     * The actual status card.
     */
    SMART_SHOP_STATE(HandleOperations.CallSmartShopCardState),

    /**
     * Info o aplikaci na terminalu.
     */
    SMART_SHOP_GET_APP_INFO(HandleOperations.CallSmartShopGetAppInfo),

    /**
     * Posledni transakce
     */
    SMART_SHOP_GET_LAST_TRAN(HandleOperations.CallSmartShopGetLastTran),

    /**
     * TMS volani pro stahnuti parametru
     */
    SMART_SHOP_PARAMETRS_CALL(HandleOperations.CallSmartShopParametersCall),

    /**
     *
     */
    SMART_SHOP_HANDSHAKE(HandleOperations.CallSmartShopHandshake),

    /**
     *
     */
    SMART_SHOP_TIP(HandleOperations.CallSmartShopTip),

    /**
     *
     */
    MAINTENANCE_UPDATE(HandleOperations.CallMaintenanceUpdate),;


    private HandleOperations operation;

    private TransactionCommand(HandleOperations operation) {
        this.setOperation(operation);
    }

    public HandleOperations getOperation() {
        return operation;
    }

    public void setOperation(HandleOperations operation) {
        this.operation = operation;
    }


}
