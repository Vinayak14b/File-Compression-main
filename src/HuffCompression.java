import java.util.*;
import java.io.*;

public class HuffCompression {
    private static StringBuilder sb = new StringBuilder();
    private static Map<Byte, String> huffmap = new HashMap<>();

    public static void compress(String src, String dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            byte[] b = new byte[inStream.available()];
            inStream.read(b);
            byte[] huffmanBytes = createZip(b);
            OutputStream outStream = new FileOutputStream(dst);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(huffmanBytes);
            objectOutStream.writeObject(huffmap);
            inStream.close();
            objectOutStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 1) It creates a FileInputStream to read the source file specified by the src
    // parameter.

    // 2) It reads the entire content of the source file into a byte array b using
    // the inStream.read(b) method.

    // 3) It calls the createZip method to compress the byte array b using Huffman
    // coding. The resulting compressed data is stored in the huffmanBytes array.

    // 4) It creates an OutputStream and ObjectOutputStream to write the compressed
    // data and the Huffman tree/map into the destination file specified by the dst
    // parameter.

    // 5) It writes the huffmanBytes array, which contains the compressed data, to
    // the ObjectOutputStream.

    // 6) It writes the huffmap, which presumably contains the Huffman tree or
    // necessary information to reconstruct it, to the ObjectOutputStream as well.

    // 7) It closes the input and output streams to release resources.

    private static byte[] createZip(byte[] bytes) {
        MinPriorityQueue<ByteNode> nodes = getByteNodes(bytes);
        ByteNode root = createHuffmanTree(nodes);
        Map<Byte, String> huffmanCodes = getHuffCodes(root);
        byte[] huffmanCodeBytes = zipBytesWithCodes(bytes, huffmanCodes);
        return huffmanCodeBytes;
    }

    // 1) getByteNodes: This is a method (not shown in the code snippet) that takes
    // the input byte array bytes and creates a minimum priority queue of ByteNode
    // objects. Each ByteNode represents a unique byte value from the input data and
    // its frequency in the array.

    // 2) createHuffmanTree: This method takes the minimum priority queue of
    // ByteNode objects and constructs the Huffman tree. The Huffman tree is built
    // by repeatedly combining the two nodes with the lowest frequencies into a new
    // node until a single root node is formed. This root node becomes the root of
    // the Huffman tree.

    // 3)getHuffCodes: This method takes the root node of the Huffman tree and
    // traverses the tree to generate Huffman codes for each unique byte value.
    // Huffman codes are binary representations that represent each byte value's
    // position in the Huffman tree. These codes are stored in a map, huffmanCodes,
    // with the byte value as the key and the corresponding Huffman code as the
    // value.

    // 4) zipBytesWithCodes: This method takes the input byte array bytes and the
    // map of Huffman codes huffmanCodes. It compresses the input byte array by
    // replacing each byte value with its corresponding Huffman code. The resulting
    // compressed data is represented as a new byte array huffmanCodeBytes.

    // 5) The method returns the huffmanCodeBytes, which contains the compressed
    // data obtained using Huffman coding.

    private static MinPriorityQueue<ByteNode> getByteNodes(byte[] bytes) {
        MinPriorityQueue<ByteNode> nodes = new MinPriorityQueue<ByteNode>();
        Map<Byte, Integer> tempMap = new HashMap<>();
        for (byte b : bytes) {
            Integer value = tempMap.get(b);
            if (value == null)
                tempMap.put(b, 1);
            else
                tempMap.put(b, value + 1);
        }
        for (Map.Entry<Byte, Integer> entry : tempMap.entrySet())
            nodes.add(new ByteNode(entry.getKey(), entry.getValue()));
        return nodes;
    }

    // 1) It initializes a MinPriorityQueue of ByteNode objects named nodes. A min
    // priority queue is a data structure that maintains its elements in ascending
    // order based on their priority.

    // 2) It initializes a temporary HashMap named tempMap, which will be used to
    // count the frequency of each byte value in the input byte array.

    // 3) It loops through each byte b in the input byte array bytes.

    // 4) For each byte b, it checks if the byte is already present in the tempMap.
    // If it is not present, it adds the byte b to the map with a frequency of 1. If
    // it is already present, it increments the frequency value by 1.

    // 5) After counting the frequencies of all byte values in the input array, it
    // then iterates through the tempMap using a for-each loop over the entrySet().

    // 6) For each entry in the tempMap, it creates a new ByteNode object using the
    // byte value as the character and its corresponding frequency as the priority.

    // 7) It adds each ByteNode to the nodes priority queue using the add method.

    // 8) Once all the byte values and their frequencies have been added as ByteNode
    // objects to the nodes priority queue, the method returns the queue.

    private static ByteNode createHuffmanTree(MinPriorityQueue<ByteNode> nodes) {
        while (nodes.len() > 1) {
            ByteNode left = nodes.poll();
            ByteNode right = nodes.poll();
            ByteNode parent = new ByteNode(null, left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            nodes.add(parent);
        }
        return nodes.poll();
    }
    // 1) The method takes the MinPriorityQueue of ByteNode objects, named nodes, as
    // input. This queue contains the ByteNode objects representing unique byte
    // values and their frequencies.

    // 2) The method runs a while loop that continues until there is only one node
    // left in the nodes priority queue. This is because the Huffman tree is
    // constructed by combining nodes until there's only one node, which becomes the
    // root of the tree.

    // 3) Inside the loop, the method performs the following steps:

    // a. It extracts the two nodes with the lowest frequencies from the front of
    // the priority queue using the poll method. These two nodes will be the left
    // and right children of the new parent node.

    // b. It creates a new ByteNode object called parent, which represents the
    // parent node of the two extracted nodes. The character (byte value) of the
    // parent node is set to null because it doesn't represent an actual byte; it
    // only serves as an internal node in the Huffman tree.

    // c. The frequencies of the left and right nodes are summed up to set the
    // frequency of the parent node.

    // d. The left and right nodes are assigned as children of the parent node by
    // setting left and right attributes accordingly.

    // e. The parent node is added back to the priority queue using the add method.
    // This ensures that the priority queue remains sorted based on the nodes'
    // frequencies.

    // 4) Once the loop is finished, there will be a single node left in the nodes
    // priority queue, which represents the root of the Huffman tree.

    // 5) The method retrieves and returns this root node by using the poll method
    // again, which removes the root node from the priority queue and returns it.

    private static Map<Byte, String> getHuffCodes(ByteNode root) {
        if (root == null)
            return null;
        getHuffCodes(root.left, "0", sb);
        getHuffCodes(root.right, "1", sb);
        return huffmap;
    }

    private static void getHuffCodes(ByteNode node, String code, StringBuilder sb1) {
        StringBuilder sb2 = new StringBuilder(sb1);
        sb2.append(code);
        if (node != null) {
            if (node.data == null) {
                getHuffCodes(node.left, "0", sb2);
                getHuffCodes(node.right, "1", sb2);
            } else
                huffmap.put(node.data, sb2.toString());
        }
    }

    // 1) The getHuffCodes method is a private helper method used in the Huffman
    // compression process to generate Huffman codes for each unique byte value in
    // the Huffman tree. Huffman codes are binary representations that represent the
    // position of each byte value in the tree.

    // Here's how the method works:

    // 2) The method takes the root node of the Huffman tree, root, as input. This
    // is the starting point for traversing the tree to generate Huffman codes for
    // each byte value.

    // 3) It first checks if the root is null. If the root is null, it means the
    // tree is empty, and thus, there are no codes to generate. In this case, the
    // method returns null.

    // 4) The method proceeds to call two overloaded versions of getHuffCodes to
    // traverse the left and right subtrees of the Huffman tree.

    // 5) The first getHuffCodes call is made with the left child of the current
    // node, root.left, and the current code as "0". This indicates that whenever we
    // move left in the tree, we append "0" to the current code.

    // 6) The second getHuffCodes call is made with the right child of the current
    // node, root.right, and the current code as "1". This indicates that whenever
    // we move right in the tree, we append "1" to the current code.

    // 7) The traversal continues recursively until leaf nodes are reached (nodes
    // with no children). At each leaf node, a complete Huffman code for a specific
    // byte value is generated.

    // 8) The method doesn't explicitly define the data structure huffmap, but it is
    // assumed that it is a global or class-level map that is used to store the
    // generated Huffman codes. The map associates each unique byte value with its
    // corresponding Huffman code.

    // 9) Once the traversal is complete, the method returns the huffmap, which
    // contains the mapping of byte values to their respective Huffman codes.

    private static byte[] zipBytesWithCodes(byte[] bytes, Map<Byte, String> huffCodes) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte b : bytes)
            strBuilder.append(huffCodes.get(b));

        int length = (strBuilder.length() + 7) / 8;
        byte[] huffCodeBytes = new byte[length];
        int idx = 0;
        for (int i = 0; i < strBuilder.length(); i += 8) {
            String strByte;
            if (i + 8 > strBuilder.length())
                strByte = strBuilder.substring(i);
            else
                strByte = strBuilder.substring(i, i + 8);
            huffCodeBytes[idx] = (byte) Integer.parseInt(strByte, 2);
            idx++;
        }
        return huffCodeBytes;
    }

    // 1) The method takes two inputs: the original byte array bytes that needs to
    // be compressed and the huffCodes map, which contains the mapping of unique
    // byte values to their respective Huffman codes.

    // 2) It initializes a StringBuilder named strBuilder to create a binary string
    // representation of the Huffman codes for the entire input byte array.

    // 3) It iterates through each byte b in the bytes array and retrieves the
    // corresponding Huffman code from the huffCodes map using huffCodes.get(b).

    // 4) It appends the retrieved Huffman code to the strBuilder, effectively
    // concatenating all the Huffman codes into a single binary string.

    // 5) The method then calculates the required length of the byte array to store
    // the compressed data. Since each Huffman code may have a variable length, the
    // method rounds up the length of the binary string to the nearest multiple of 8
    // (8 bits per byte) using the expression (strBuilder.length() + 7) / 8. This
    // will ensure that the byte array has enough space to store the compressed data
    // without any loss.

    // 6) It creates a new byte array huffCodeBytes with the calculated length to
    // hold the compressed data.

    // 7) The method then converts the binary string representation of the Huffman
    // codes into bytes and stores them in the huffCodeBytes array. It does this by
    // iterating through the strBuilder in chunks of 8 bits (one byte) at a time.

    // 8) For each chunk, it checks if there are fewer than 8 bits remaining. If so,
    // it takes the remaining bits to form a partial byte. Otherwise, it takes the
    // next 8 bits.

    // 9) The method converts the binary string representation of the byte to an
    // actual byte value using Integer.parseInt(strByte, 2) and stores it in the
    // huffCodeBytes array.

    // 10) Finally, the method returns the huffCodeBytes array, which now contains
    // the compressed data represented as bytes.

    public static void decompress(String src, String dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            byte[] huffmanBytes = (byte[]) objectInStream.readObject();
            Map<Byte, String> huffmanCodes = (Map<Byte, String>) objectInStream.readObject();

            byte[] bytes = decomp(huffmanCodes, huffmanBytes);
            OutputStream outStream = new FileOutputStream(dst);
            outStream.write(bytes);
            inStream.close();
            objectInStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] decomp(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);
            sb1.append(convertbyteInBit(!flag, b));
        }
        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        java.util.List<Byte> list = new java.util.ArrayList<>();
        for (int i = 0; i < sb1.length();) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                String key = sb1.substring(i, i + count);
                b = map.get(key);
                if (b == null)
                    count++;
                else
                    flag = false;
            }
            list.add(b);
            i += count;
        }
        byte b[] = new byte[list.size()];
        for (int i = 0; i < b.length; i++)
            b[i] = list.get(i);
        return b;
    }

    private static String convertbyteInBit(boolean flag, byte b) {
        int byte0 = b;
        if (flag)
            byte0 |= 256;
        String str0 = Integer.toBinaryString(byte0);
        if (flag || byte0 < 0)
            return str0.substring(str0.length() - 8);
        else
            return str0;
    }

    public static void main(String[] args) {
        compress("C:\\Users\\lotus\\OneDrive\\Desktop\\virat.txt ",
                "C:\\Users\\lotus\\OneDrive\\Desktop\\viratcompress.txt");

    }
}