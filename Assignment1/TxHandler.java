import java.util.ArrayList;
import java.util.HashMap;

public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */

    public UTXOPool pool = null;

    public TxHandler(UTXOPool utxoPool) {
        this.pool = new UTXOPool(utxoPool);
    }

    /** Returns true if
     * (1) all outputs claimed by tx are in the current UTXO pool
     * (2) the signatures on each input of tx are valid
     * (3) no UTXO is claimed multiple times by tx
     * (4) all of tx output values are non-negative
     * (5) the sum of tx input values is greater than or equal
     * to the sum of its output values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        int i = 0;
        //memorizing coins spent in transaction to detect double-spend attacks
        HashMap<UTXO, Integer> memo = new HashMap<UTXO, Integer>();

        // sum of claimed outputs >= sum of created outputs
        Double in_values = 0.0; // claimed outputs
        Double out_values = 0.0; // created outputs

        // checking inputs
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        for(Transaction.Input in : inputs) {
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
            // fake coin check
            if(this.pool.contains(utxo) == false) {
                return false;
            }

            Transaction.Output out = this.pool.getTxOutput(utxo);

            // invalid signature check
            if(!Crypto.verifySignature(out.address, tx.getRawDataToSign(i), in.signature)) {
                return false;
            }

            in_values += out.value;
            // double spend attack check
            if(memo.containsKey(utxo)) {
                return false;
            }
            memo.put(utxo, 1);
            i++;
        }
        // all inputs should be okay for now
        // checking outputs
        ArrayList<Transaction.Output> outputs = tx.getOutputs();
        i = 0;
        for(Transaction.Output out : outputs) {
            // negative outputs check
            if(out.value < 0.0) {
                return false;
            }
            out_values += out.value;
            i++;
        }

        // consistent values check
        if(in_values < out_values) {
            return false;
        }

        // all good :)
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        //Accepted transactions
        ArrayList<Transaction> ret = new ArrayList<Transaction>();

        int i = 0;
        for(Transaction tx : possibleTxs) {
            if(isValidTx(tx)) {
                int j = 0;
                //spending un-spent outputs
                for(Transaction.Input in : tx.getInputs()) {
                    UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
                    pool.removeUTXO(utxo);
                    j++;
                }
                j = 0;
                //adding newly created outputs (they're unspent of course .. they're new!)
                for(Transaction.Output out : tx.getOutputs()) {
                    UTXO utxo = new UTXO(tx.getHash(), j);
                    pool.addUTXO(utxo, out);
                    j++;
                }
                ret.add(tx);
                i = -1;
            }
            i++;
        }
        //a little bit of dirty work
        Transaction[] tr = new Transaction[ret.size()];
        i = 0;
        for(Transaction tx : ret) {
            tr[i] = new Transaction(tx);
            i++;
        }
        return tr;
    }

}
