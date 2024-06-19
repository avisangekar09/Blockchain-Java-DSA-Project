import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Scanner;

class Block {
    // private int index;
    // private long timestamp;
    // private String data;
    // private String previousHash;
    // private String hash;

      int index;
      long timestamp;
      String data;
      String previousHash;
      String hash;
    
    public Block(int index, long timestamp, String data, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }
    
    public String calculateHash() {
        String dataToHash = index + timestamp + data + previousHash;
        StringBuilder sb = new StringBuilder();
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(dataToHash.getBytes());
            
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return sb.toString();
    }
    
    public String getHash() {
        return hash;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String newData) {
        this.data = newData;
        this.hash = calculateHash();
    }
}

class Blockchain {
    public LinkedList<Block> chain;
    
    public Blockchain() {
        chain = new LinkedList<>();
        // Genesis block
        chain.add(createGenesisBlock());
    }
    
    private Block createGenesisBlock() {
        return new Block(0, System.currentTimeMillis(), "Genesis Block", "0");
    }
    
    public Block getLatestBlock() {
        return chain.getLast();
    }
    
    public void addBlock(Block newBlock) {
        newBlock.previousHash = getLatestBlock().getHash();
        newBlock.hash = newBlock.calculateHash();
        chain.add(newBlock);
    }
    
    public void deleteBlock(int index) {
        if (index < 1 || index >= chain.size()) {
            System.out.println("Invalid index! Please enter a valid index.");
            return;
        }
        chain.remove(index);
        rehashBlocks();
    }
    
    private void rehashBlocks() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            currentBlock.previousHash = chain.get(i - 1).getHash();
            currentBlock.hash = currentBlock.calculateHash();
        }
    }
    
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            
            if (!previousBlock.getHash().equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }
    
    public void updateTransaction(int index, String newData) {
        if (index < 1 || index >= chain.size()) {
            System.out.println("Invalid index! Please enter a valid index.");
            return;
        }
        chain.get(index).setData(newData);
    }
}

public class BlockchainDemo {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n1. Add Transaction");
            System.out.println("2. Update Transaction");
            System.out.println("3. Delete Transaction");
            System.out.println("4. View Blockchain");
            System.out.println("5. Verify Blockchain");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    System.out.print("Enter transaction data: ");
                    scanner.nextLine(); // consume newline
                    String data = scanner.nextLine();
                    blockchain.addBlock(new Block(blockchain.chain.size(), System.currentTimeMillis(), data, blockchain.getLatestBlock().getHash()));
                    System.out.println("Transaction added successfully!");
                    break;
                    
                case 2:
                    System.out.print("Enter index of transaction to update: ");
                    int updateIndex = scanner.nextInt();
                    System.out.print("Enter new data: ");
                    scanner.nextLine(); // consume newline
                    String newData = scanner.nextLine();
                    blockchain.updateTransaction(updateIndex, newData);
                    System.out.println("Transaction updated successfully!");
                    break;
                    
                case 3:
                    System.out.print("Enter index of transaction to delete: ");
                    int deleteIndex = scanner.nextInt();
                    blockchain.deleteBlock(deleteIndex);
                    System.out.println("Transaction deleted successfully!");
                    break;
                    
                case 4:
                    System.out.println("\nBlockchain:");
                    for (Block block : blockchain.chain) {
                        System.out.println("Index: " + block.index);
                        System.out.println("Timestamp: " + block.timestamp);
                        System.out.println("Data: " + block.getData());
                        System.out.println("Previous Hash: " + block.previousHash);
                        System.out.println("Hash: " + block.getHash());
                        System.out.println();
                    }
                    break;
                    
                case 5:
                    System.out.println("Is blockchain valid? " + blockchain.isChainValid());
                    break;
                    
                case 6:
                    System.out.println("Exiting program...");
                    scanner.close();
                    System.exit(0);
                    
                default:
                    System.out.println("Invalid choice! Please try again.");
                    break;
            }
        }
    }
}
