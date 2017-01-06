## Assignment 1 Description
In this assignment, you are asked to implement the scrooge coin ledger, I'll go through clarifying the assignment, then try to solve it on your own.

A transaction is identified by a hash (the id of the transaction), each transaction consists of inputs and outputs.

An output is like a coin .. it has a value (coin's value) and a public address of it's owner, he's called the recipient because someone has signed a pay request to give this coin to that person .. thus he is a recipient, but the new owner as well.
Inputs are like hash-pointers to actual coins, they have a hash for the transaction in which the coin has been created(spent), and also the index of the list in which the coin resides.

So a transaction must do a pay of some coins to some people, in order for the transaction to know which coins are going to be paid, it mush have inputs, so inputs tell the transaction which coins are going to be used in this transaction, in order for an input to correctly reference a coin(output) it needs to reach the transaction in which the coin was created(spent), and also the index inside of that transaction.

So far a transaction has been fed with input coins, and now it may either spend them, either to someone, or it may create a new bigger coin out of those smaller ones, or it may re-partition them in whatever ways, in all cases input coins are spent, and new output coins may appear, this is what output means.

In code delivered with the assignments, a coin in found in the Transaction.Output class, an instance of it represents a coin, an unspent coin is called UnspentTransactionOutput is the UTXO class, it's simply a hash-pointer(or similar) to reference a coin created in a specific transaction located by index.

A pool to have all those unspent coins is also defined to keep track of them.

So the handler needs to handle some transactions and choose a subset of them that are valid among each other.

The handler takes an array of transactions, the array may not be sorted logically, for example the first transaction may reference a coin that is created in the second transaction, so the second transaction must be processed before the first on in order to be valid, you'll have to handle that :)

[Check this gist](https://gist.github.com/mentlsve/ef15013f1e6e5abd82996b34a7b4131b), it'll help you test your code.

I guess that's it .. happy coding :)
